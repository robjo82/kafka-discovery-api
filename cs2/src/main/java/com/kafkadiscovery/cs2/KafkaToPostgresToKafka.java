package com.kafkadiscovery.cs2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkadiscovery.cs2.model.Address;
import com.kafkadiscovery.cs2.model.Movement;
import com.kafkadiscovery.cs2.model.Person;
import com.kafkadiscovery.cs2.model.Stay;
import com.kafkadiscovery.cs2.producer.Producer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KafkaToPostgresToKafka {
    private static final Logger logger = Logger.getLogger(KafkaToPostgresToKafka.class.getName());

    private static final String KAFKA_BROKER = System.getenv("KAFKA_BROKER");
    private static final List<String> TOPICS = Arrays.asList("topic2", "topic3", "topic4");
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static volatile boolean running = true;
    private static final String STOP = "stop";
    private static final String GET_ALL_PATIENTS_SQL = "SELECT * FROM public.person";
    private static final String GET_PATIENT_BY_PID_SQL = "SELECT * FROM public.person WHERE id = ?";
    private static final String GET_PATIENT_BY_NAME_SQL = "SELECT * FROM public.person WHERE lower(firstname) LIKE ? OR lower(lastname) LIKE ?";



    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        logger.info("Tentative de connexion au serveur Kafka à l'adresse " + KAFKA_BROKER);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            logger.info("Connecté au serveur Kafka");
            consumer.subscribe(TOPICS);

            ObjectMapper objectMapper = new ObjectMapper();

            logger.info("Tentative de connexion à la base de données à l'URL " +  DB_URL);
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                logger.info("Connecté à la base de données PostgreSQL");

                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> consumerRecord : records) {
                        if (TOPICS.get(2).equals(consumerRecord.topic()) && STOP.equals(consumerRecord.value())) {
                            logger.info("Received stop signal");
                            stop();
                            break;
                        }

                        if (TOPICS.get(0).equals(consumerRecord.topic())){
                            logger.info("Processing record from topic1");
                            processRecord(objectMapper.readTree(consumerRecord.value()), connection);
                        }

                        logger.info("offset = " + consumerRecord.offset() + ", key = " + consumerRecord.key() + ", value = " + consumerRecord.value());
                    }
                    consumer.commitSync();
                }
            }
        } catch (Exception e) {
            logger.severe("Error while consuming records" + e.getMessage());
        }
    }

    private static void processRecord(JsonNode jsonNode, Connection connection) {
        Producer producer = new Producer(KAFKA_BROKER, TOPICS.get(1));
        switch (jsonNode.get("command").textValue()) {
            case "get_all_patients":
                try {
                    List<Person> patients = getAllPatients(connection);
                    logger.info("Sending patients to kafka");
                    producer.sendRecord(patients.toString());
                } catch (SQLException e) {
                    logger.severe("Error while getting all patients" + e.getMessage());
                }
                break;
            case "get_patient_by_pid":
                try {
                    Person person = getPatientByPid(connection, jsonNode.get("pid").asLong());
                    logger.info("Sending patient to kafka");
                    producer.sendRecord(person.toString());
                } catch (SQLException e) {
                    logger.severe("Error while getting patient by pid" + e.getMessage());
                }
                break;
            case "get_patient_by_name":
                try {
                    List<Person> patients = getPatientByName(connection, jsonNode.get("name").textValue());
                    logger.info("Sending patients to kafka");
                    producer.sendRecord(patients.toString());
                } catch (SQLException e) {
                    logger.severe("Error while getting patient by name" + e.getMessage());
                }
                break;
            case "get_patient_stay_by_pid":
                try {
                    List<Stay> stays = getPatientStayByPid(connection, jsonNode.get("pid").asLong());
                    logger.info("Sending stays to kafka");
                    producer.sendRecord(stays.toString());
                } catch (Exception e) {
                    logger.severe("Error while getting patient stay by pid" + e.getMessage());
                }
                break;
            case "get_patient_movements_by_sid":
                try {
                    List<Movement> movements = getPatientMovementsBySid(connection, jsonNode.get("sid").asLong());
                    logger.info("Sending movements to kafka");
                    producer.sendRecord(movements.toString());
                } catch (Exception e) {
                    logger.severe("Error while getting patient movements by sid" + e.getMessage());
                }
                break;
            case "export_data":
                try {
                    exportData(connection);
                    logger.info("Data exported");
                    producer.sendRecord("Data exported");
                } catch (Exception e) {
                    logger.severe("Error while exporting data" + e.getMessage());
                }
                break;
            default:
                logger.log(Level.SEVERE,"Invalid command: ${{jsonNode.get(\"command\").textValue())}");
        }
    }

    public static void stop() {
        running = false;
    }

    public static List<Person> getAllPatients(Connection connection) throws SQLException {
        List<Person> patients = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_ALL_PATIENTS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Person person = new Person();
                person.setId(resultSet.getInt("id"));
                person.setFirstname(resultSet.getString("firstname"));
                person.setLastname(resultSet.getString("lastname"));
                person.setStreet(resultSet.getString("street"));
                person.setCity(resultSet.getString("city"));
                person.setPostalCode(resultSet.getString("postal_code"));
                patients.add(person);
            }
        }
        return patients;
    }

    public static Person getPatientByPid(Connection connection, Long pid) throws SQLException {
        Person person = null;
        try (PreparedStatement statement = connection.prepareStatement(GET_PATIENT_BY_PID_SQL)) {

            statement.setLong(1, pid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                person = new Person();
                person.setId(resultSet.getInt("id"));
                person.setFirstname(resultSet.getString("firstname"));
                person.setLastname(resultSet.getString("lastname"));
                person.setAddress(new Address(
                        resultSet.getString("street"),
                        resultSet.getString("city"),
                        resultSet.getString("postalCode")
                ));
            }
        }
        return person;
    }

    public static List<Person> getPatientByName(Connection connection, String name) throws SQLException {
        List<Person> patients = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_PATIENT_BY_NAME_SQL)) {

            statement.setString(1, "%" + name.toLowerCase() + "%");
            statement.setString(2, "%" + name.toLowerCase() + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Person person = new Person();
                person.setId(resultSet.getInt("id"));
                person.setFirstname(resultSet.getString("firstname"));
                person.setLastname(resultSet.getString("lastname"));

                person.setAddress(new Address(
                        resultSet.getString("street"),
                        resultSet.getString("city"),
                        resultSet.getString("postal_code")
                ));

                patients.add(person);
            }
        }
        return patients;
    }

    private static List<Stay> getPatientStayByPid(Connection connection, long pid) {

    }


}
