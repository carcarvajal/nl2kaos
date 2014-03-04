package nl2kaos;

public class Operation {
	//Cada operación tienen un verbo y un objeto
	private String verbo;
        private String lemma;
	private String objeto;
	
	public Operation(String verbo, String lemma, String objeto) {
		super();
		this.verbo = verbo;
		this.objeto = objeto;
                this.lemma=lemma;
	}
        public Operation(String verbo, String objeto) {
		super();
		this.verbo = verbo;
		this.objeto = objeto;
	}
	public String getVerbo() {
		return verbo;
	}
        public String getLemma() {
		return lemma;
	}
	public void setVerbo(String verbo) {
		this.verbo = verbo;
	}
        public void setVerbo(String verbo, String lemma) {
		this.verbo = verbo;
                this.lemma = lemma;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

}
