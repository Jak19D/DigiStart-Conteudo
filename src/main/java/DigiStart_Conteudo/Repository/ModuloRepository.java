package DigiStart_Conteudo.Repository;

import DigiStart_Conteudo.Model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findByAtivoTrue();

    List<Modulo> findByProfessorId(Long professorId);

    List<Modulo> findByProfessorIdAndAtivoTrue(Long professorId);
}
