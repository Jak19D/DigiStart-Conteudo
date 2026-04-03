package DigiStart_Conteudo.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "modulos")
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do módulo é obrigatório")
    @Size(min = 5, max = 50, message = "O nome deve ter entre 5 e 50 caracteres")
    private String nome;

    @Size(max = 255)
    private String descricao;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(name = "professor_id")
    private Long professorId;

    public Modulo() {}

    public Modulo(String nome) {
        this.nome = nome;
    }

    public Modulo(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Modulo(String nome, String descricao, Long professorId) {
        this.nome = nome;
        this.descricao = descricao;
        this.professorId = professorId;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    @Override
    public String toString() {
        return "Modulo{id=" + id + ", nome='" + nome + "'}";
    }
}
