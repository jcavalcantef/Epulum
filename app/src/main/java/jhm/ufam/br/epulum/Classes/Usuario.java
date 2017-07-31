package jhm.ufam.br.epulum.Classes;

        import java.io.Serializable;

/**
 * Created by jonathascavalcante on 28/07/17.
 */

public class Usuario implements Serializable{
    private String id;
    private String email;
    private String nome;
    private String senha;
    private String foto;

    public Usuario(){}
    public Usuario(String idNovo, String emailNovo, String nomeNovo, String senhaNovo, String fotoNovo){
        this.id = idNovo;
        this.email = emailNovo;
        this.nome = nomeNovo;
        this.senha = senhaNovo;
        this.foto = fotoNovo;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
