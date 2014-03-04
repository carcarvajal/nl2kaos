package nl2kaos;

import java.util.*;

public class Agent {
	//Un agente realiza operaciones
	//Un agente tiene nombre, definición y una o varias operaciones
	private String name;
	private String lemma;
	private String def;
        private String synset;
	private String category;
	private int freq;
        private int posicion;
	
	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public String getCategory() {
		return category;
	}
        public int getNOp(){
            return op.size();
        }

	public void setCategory(String category) {
		this.category = category;
	}
	private ArrayList<Operation> op=new ArrayList<Operation>();
	
	public Agent(){
		def="";
	}
	
	public Agent(String name, int freq) {
		super();
		this.name = name;
		category=null;
		this.freq=freq;
	}
	public Agent(String name, String lemma) {
		super();
		this.name = name;
		this.lemma=lemma;
		category=null;
	}
        
        public Agent(String name, String lemma,int posicion) {
		super();
		this.name = name;
		this.lemma=lemma;
		category=null;
                this.posicion=posicion;
	}
        public Agent(String name, String lemma, String synset) {
		super();
		this.name = name;
		this.lemma=lemma;
                this.synset=synset;
		category=null;
	}
	public Agent(String name, Operation op) {
		super();
		this.name = name;
		this.op.add(new Operation(op.getVerbo(),op.getObjeto()));
		category=null;
		freq=1;
	}
	public Agent(String name) {
		super();
		this.name = name;
		category=null;
		freq=1;
	}
	public String getName() {
		return name;
	}
        public String getSynset() {
		return synset;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDef() {
		return def;
	}
	public void setDef(String def) {
		this.def = def;
	}
	public Operation getOp(int n) {
		return op.get(n);
	}
	public void setOp(Operation op) {
		this.op.add(op);
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
        public int getPosicion(){
            return posicion;
        }
}
