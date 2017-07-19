package jhm.ufam.br.epulum.Classes;


public class Categoria {
    private long _id;
    private String nome;
    private String tipo;

  public Categoria() {
    nome = "";
    tipo = "";
  }

  public Categoria(String nome, String tipo) {
    this.nome = nome;
    this.tipo = tipo;
  }

  public Categoria(long _id, String nome, String tipo) {
    this._id = _id;
    this.nome = nome;
    this.tipo = tipo;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public long get_id() {
    return _id;
  }

  public void set_id(long _id) {
    this._id = _id;
  }
}