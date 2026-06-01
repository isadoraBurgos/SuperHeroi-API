package senac.tsi.superhero.apikey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByChaveAndAtivaTrue(String chave);

    boolean existsByChave(String chave);
}
