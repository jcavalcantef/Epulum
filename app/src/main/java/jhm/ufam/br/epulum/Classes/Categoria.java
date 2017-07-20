package jhm.ufam.br.epulum.Classes;


public class Categoria {
    private String nome;
    private long tipo;

  public Categoria() {
    nome = "";
  }

  public Categoria(String nome, long tipo) {
    this.nome = nome;
    this.tipo = tipo;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public long getTipo() {
    return tipo;
  }

  public void setTipo(long tipo) {
    this.tipo = tipo;
  }
}