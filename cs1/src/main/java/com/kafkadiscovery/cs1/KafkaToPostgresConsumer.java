package com.kafkadiscovery.cs1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KafkaToPostgresConsumer {
    private static final Logger logger = Logger.getLogger(KafkaToPostgresConsumer.class.getName());

    private static final String KAFKA_BROKER = System.getenv("KAFKA_BROKER");
    private static final List<String> TOPICS = Arrays.asList("topic1", "topic4");
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static volatile boolean running = true;
    private static final String STOP = "stop";
    private static final String PERSON = "person";
    private static final String ADDRESS = "address";
    private static final String STAYS = "stays";
    private static final String MOVEMENTS = "movements";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String STREET = "street";
    private static final String CITY = "city";
    private static final String POSTAL_CODE = "postalCode";
    private static final String STAY_NUMBER = "stayNumber";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String SERVICE = "service";
    private static final String ROOM = "room";
    private static final String BED = "bed";
    private static final String DATE = "date";
    private static final String INSERT_PERSON_SQL = "INSERT INTO public.person (firstname, lastname, street, city, postal_code) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String INSERT_STAY_SQL = "INSERT INTO public.stay (person_id, stay_number, start_date, end_date) VALUES (?, ?, ?, ?)";
    private static final String INSERT_MOVEMENT_SQL = "INSERT INTO public.movement (person_id, stay_number, service, room, bed, date) VALUES (?, ?, ?, ?, ?, ?)";

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
                connection.setAutoCommit(false);

                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> consumerRecord : records) {
                        if (TOPICS.get(1).equals(consumerRecord.topic()) && STOP.equals(consumerRecord.value())) {
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
        if (jsonNode.has(PERSON) && jsonNode.has(ADDRESS) && jsonNode.has(STAYS) && jsonNode.has(MOVEMENTS)) {
            insertData(connection, jsonNode);
        } else {
            logger.log(Level.SEVERE, "Invalid record: ${jsonNode.toString()}");
        }
    }

    private static void insertData(Connection connection, JsonNode jsonNode) {
        long personId = 0;
        try (PreparedStatement preparedStatementPerson = connection.prepareStatement(INSERT_PERSON_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatementPerson.setString(1, jsonNode.get(PERSON).get(FIRSTNAME).textValue());
            preparedStatementPerson.setString(2, jsonNode.get(PERSON).get(LASTNAME).textValue());
            preparedStatementPerson.setString(3, jsonNode.get(ADDRESS).get(STREET).textValue());
            preparedStatementPerson.setString(4, jsonNode.get(ADDRESS).get(CITY).textValue());
            preparedStatementPerson.setInt(5, jsonNode.get(ADDRESS).get(POSTAL_CODE).asInt());
            preparedStatementPerson.executeUpdate();

            ResultSet resultSet = preparedStatementPerson.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                personId = resultSet.getLong(1);
            }

            for (JsonNode stayNode : jsonNode.get(STAYS)) {
                insertStaySQL(connection, stayNode, personId);
            }
            for (JsonNode movementNode : jsonNode.get(MOVEMENTS)) {
                insertMovementSQL(connection, movementNode, personId);
            }

            connection.commit();
        } catch (Exception e) {
            logger.severe("Error while inserting data " + e.getMessage());

            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.severe("Error while rolling back transaction" + ex.getMessage());
            }
        }
    }

    private static void insertStaySQL(Connection connection, JsonNode stayNode, long personId) {
        try (PreparedStatement preparedStatementStay = connection.prepareStatement(INSERT_STAY_SQL)) {
            preparedStatementStay.setLong(1, personId);
            preparedStatementStay.setInt(2, stayNode.get(STAY_NUMBER).asInt());
            preparedStatementStay.setDate(3, Date.valueOf(stayNode.get(START_DATE).asText()));
            preparedStatementStay.setDate(4, Date.valueOf(stayNode.get(END_DATE).asText()));
            preparedStatementStay.executeUpdate();
        } catch (Exception e) {
            logger.severe("Error while inserting stay" + e.getMessage());
        }
    }

    private static void insertMovementSQL(Connection connection, JsonNode movementNode, long personId) {
        try (PreparedStatement preparedStatementMovement = connection.prepareStatement(INSERT_MOVEMENT_SQL)) {
            preparedStatementMovement.setLong(1, personId);
            preparedStatementMovement.setInt(2, movementNode.get(STAY_NUMBER).asInt());
            preparedStatementMovement.setString(3, movementNode.get(SERVICE).textValue());
            preparedStatementMovement.setString(4, movementNode.get(ROOM).textValue());
            preparedStatementMovement.setString(5, movementNode.get(BED).textValue());
            preparedStatementMovement.setDate(6, Date.valueOf(movementNode.get(DATE).asText()));
            preparedStatementMovement.executeUpdate();
        } catch (Exception e) {
            logger.severe("Error while inserting movement" + e.getMessage());
        }
    }

    public static void stop() {
        running = false;
    }
}
