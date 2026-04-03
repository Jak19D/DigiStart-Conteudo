package DigiStart_Conteudo.Mapper;

import DigiStart_Conteudo.DTO.Input.ExercicioRequestDTO;
import DigiStart_Conteudo.DTO.Output.ExercicioResponseDTO;
import DigiStart_Conteudo.Model.Exercicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExercicioMapper {
    ExercicioResponseDTO toResponseDTO(Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aula", ignore = true)
    Exercicio toEntity(ExercicioRequestDTO dto);
}
