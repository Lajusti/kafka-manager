package alejandro.lajusticia.connector.repository;

import alejandro.lajusticia.connector.entity.Connector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectorJpaRepository extends JpaRepository<Connector, Long> {
}
