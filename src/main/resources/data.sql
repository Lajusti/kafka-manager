INSERT INTO TOPIC
    (ID, BOOTSTRAP, DATE, GROUP_ID, NAME, KEY_SERIALIZER, VALUE_SERIALIZER, SCHEMA_REGISTRY)
VALUES
    (1, 'localhost:9092', '2019-01-03 09:26:48.93', 'bfdfe46f-beb3-4615-8e58-d10af7b461c9', 'topic-avro-value', 0, 1, 'http://localhost:8081'),
    (2, 'localhost:9092', '2019-01-03 09:26:48.93', 'bfdfe46f-beb3-4615-8e58-d10af7b461c2', 'test-topic', 0, 0, '');

INSERT INTO CONNECTOR
    (ID, NAME, URL)
VALUES
    (1, 'CON T1', 'http://localhost/connectors/ConnectorTest1'),
    (2, 'CON T2', 'http://localhost/connectors/ConnectorTest2'),
    (3, 'CON T3', 'http://localhost/connectors/ConnectorTest3');