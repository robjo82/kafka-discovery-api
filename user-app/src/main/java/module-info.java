module com.kafkadiscovery.userapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.kafkadiscovery.userapp to javafx.fxml;
    exports com.kafkadiscovery.userapp;
}