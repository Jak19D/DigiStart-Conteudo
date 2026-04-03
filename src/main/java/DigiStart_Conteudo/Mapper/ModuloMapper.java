package DigiStart_Conteudo.Mapper;

import DigiStart_Conteudo.DTO.Input.ModuloRequestDTO;
import DigiStart_Conteudo.DTO.Output.ModuloResponseDTO;
import DigiStart_Conteudo.Model.Modulo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuloMapper {

    ModuloResponseDTO toResponseDTO(Modulo modulo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professorId", ignore = true)
    Modulo toEntity(ModuloRequestDTO dto);
}
