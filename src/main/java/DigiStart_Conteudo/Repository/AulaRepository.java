package DigiStart_Conteudo.Repository;

import DigiStart_Conteudo.Model.Modulo;
import DigiStart_Conteudo.Model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    List<Aula> findByModuloIdOrderByOrdemAsc(Long moduloId);

    Optional<Aula> findByModuloAndOrdem(Modulo modulo, int ordem);

    int countByModulo(Modulo modulo);
}