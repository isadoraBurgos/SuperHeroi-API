package senac.tsi.superhero.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.superhero.entities.Poder;

@Repository
public interface PoderRepository extends JpaRepository<Poder, Long> {

    Page<Poder> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    boolean existsByNomeIgnoreCase(String nome);

}
