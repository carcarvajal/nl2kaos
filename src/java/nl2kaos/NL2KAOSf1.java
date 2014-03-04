/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl2kaos;

import connSQL.ConnSQL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import wordnet.similarity.SimilarityAssessor;
import wordnet.similarity.WordNotFoundException;
import edu.upc.freeling.*;
import gui.Resultados;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import similarity.AdaptedLesk;
import similarity.JWS;

/**
 *
 * @author personal
 */
public class NL2KAOSf1 {

    private static final String DATA = "/usr/local/share/freeling/";
    private static final String LANG = "es";
    private static ArrayList<String> stopwords = new ArrayList<String>();//Lista predefinida de stopwords español
    private static ArrayList<Agent> ags = new ArrayList<Agent>();
    private static ArrayList<Entity> ents = new ArrayList<Entity>();
    private static ArrayList<Goal> gls = new ArrayList<Goal>();
    private static ArrayList<Operation> ops = new ArrayList<Operation>();
    private static SimilarityAssessor _assessor;
    private static PrintWriter writer;
    private static ArrayList<ArrayList<String>> verbosense = new ArrayList<ArrayList<String>>();
    private static Map<String, ArrayList<String>> defs = new HashMap<String, ArrayList<String>>();
    private static Map<String, String> palabrasdesamb = new HashMap<String, String>();
    private static ConnSQL mn;
    private static String[] vbsOp;
    private static boolean proxy = false;
    private static String desa = "MFS";//"UKB";
    private String text;
    private static int simi = 1;//1: javasimmlib. 2: CALPS
    Tokenizer tk;
    Splitter sp;
    Maco mf;
    HmmTagger tg;
    ChartParser parser;
    DepTxala dep;
    Nec neclass;
    UkbWrap dis;
    Senses sen;
    static JWS ws;
    static AdaptedLesk lesk;

    public void iniciar() throws UnsupportedEncodingException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        File file = new File("sw");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                stopwords.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setProperty("java.library.path", "/home/unalmed/Documentos/freeling-3.0/APIs/java");
        java.lang.reflect.Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);

        System.loadLibrary("freeling_javaAPI");

        Util.initLocale("default");

        // Create options set for maco analyzer.
        // Default values are Ok, except for data files.
        MacoOptions op = new MacoOptions(LANG);

        op.setActiveModules(
                false, true, true, true, true, true, true, true, true, true, false);

        op.setDataFiles(
                "",
                DATA + LANG + "/locucions.dat",
                DATA + LANG + "/quantities.dat",
                DATA + LANG + "/afixos.dat",
                DATA + LANG + "/probabilitats.dat",
                DATA + LANG + "/dicc.src",
                DATA + LANG + "/np.dat",
                DATA + "common/punct.dat",
                DATA + LANG + "/corrector/corrector.dat");

        // Create analyzers.
        tk = new Tokenizer(DATA + LANG + "/tokenizer.dat");
        sp = new Splitter(DATA + LANG + "/splitter.dat");
        mf = new Maco(op);

        tg = new HmmTagger(LANG, DATA + LANG + "/tagger.dat", true, 2);
        parser = new ChartParser(
                DATA + LANG + "/chunker/grammar-chunk.dat");
        dep = new DepTxala(DATA + LANG + "/dep/dependences.dat",
                parser.getStartSymbol());
        neclass = new Nec(DATA + LANG + "/nec/nec-ab.dat");

        dis = new UkbWrap(DATA + LANG + "/ukb.dat");

        sen = new Senses(DATA + "es/senses.dat");

        String dir = "/home/personal/WordNet-";
        ws = new JWS(dir, "3.0");
        lesk = ws.getAdaptedLesk();

        vbsOp = "go_away.2,exit.1,come.19,go_out.2,go_out.3,depart.6,date.1,fall.28,log_off.1,develop.2,develop.1,develop.7,build_up.5,open.6,clear.1,ease_up.1,move_out.1,take_away.3,draw.13,pull_back.3,recall.6,replace.2,replace.1,exchange.5,create.1,do.3,carry_out.2,effect.2,give.5,lend.2,open.1,open.2,open.4,open.5,unlock.3,open.9,fill.1,shove.3,take_in.12,direct.6,send.4,mail.2,despatch.1,get_off.3,remit.1,message.2,do.2,perform.3,play.6,interpret.3,execute.7,present.4,reassign.1,transfer.2,shift.3,transfer.9,shuffle.2,transfer.5,pass_on.4,call.5,ascertain.2,fasten.1,assure.2,insure.4,brace_up.1,batten.1,disengage.2,free.5,free.11,break_up.5,break.1,discontinue.2,abort.1,look_for.1,record.2,register.1,get_down.6,comb.2,enter.5,look_for.1,record.2,register.1,get_down.6,comb.2,ask.2,bespeak.2,beg.2,allow.4,reserve.3,regenerate.1,resume.3,freshen_up.1,reshape.1,glance_over.1,scan.3,read.4,assay.2,look_for.1,look.4,explore.1,call_for.4,go_after.2,search.4,beat_about.1,hunt.6,carry.28,guard.2,guard.1,gather_in.1,stow.1,put_aside.2,read.3,read.4,decipher.2,appraise.1,do_away_with.1,eliminate.2,abolish.1,bump_off.1,drop.5,remove.2,eliminate.4,wash_away.1,cancel.4,write_off.3,calculate.1,approximate.2,calculate.2,calculate.4,account.2,give.6,give.3,give.4,give.17,devote.2,gift.2,bear.5,apply.6,give_away.1,bestow.2,give.29,process.1,process.6,action.1,process.7,prosecute.1,decide.1,determine.3,determine.2,define.5,ascertain.3,determine.7,calculate.4,judge.1,rubberstamp.2,advertise.2,announce.3,take.1,get_hold_of.1,drink.1,take.21,booze.1,seize.2,appropriate.2,pick_up.9,take_in.1,absorb.6,print.1,bring_out.3,compose.3,write.2,drop_a_line.1,write.7,localise.3,localise.1,constitute.4,service.1,repair.1,complete.5,test.1,search.2,choose.1,accept.1,accept.2,accept.3,".split(",");
        if (proxy) {
            System.setProperty("http.proxyHost", "proxy.medellin.unal.edu.co");
            System.setProperty("http.proxyPort", "8080");
        }

        // Instead of "UkbWrap", you can use a "Senses" object, that simply
        // gives all possible WN senses, sorted by frequency.
        // Senses dis = new Senses(DATA+LANG+"/senses.dat");
        //
        // Make sure the encoding matches your input text (utf-8, iso-8859-15, ...)
    }

    public void setTexto(String texto) {
        try {
            iniciar();
            System.out.println("teeee "+texto);
        } catch (Exception ex) {
        }
        this.text = texto;
    }

    public void procesar() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, WordNotFoundException {

        ags = new ArrayList<Agent>();
        ents = new ArrayList<Entity>();
        gls = new ArrayList<Goal>();
        ops = new ArrayList<Operation>();
        verbosense = new ArrayList<ArrayList<String>>();
        defs = new HashMap<String, ArrayList<String>>();
        palabrasdesamb = new HashMap<String, String>();

         //String line = input.readLine();
        // Extract the tokens from the line of text.
        ListWord l = tk.tokenize(text);

        // Split the tokens into distinct sentences.
        ListSentence ls = sp.split(l, false);

        // Perform morphological analysis
        mf.analyze(ls);

        // Perform part-of-speech tagging.
        tg.analyze(ls);

        // Perform named entity (NE) classificiation.
        neclass.analyze(ls);

        if (desa.equals("MFS")) {
            sen.analyze(ls);//MFS
        } else {
            dis.analyze(ls);//UKB
        }

        printResults(ls, "tagged");

        // Chunk parser
        parser.analyze(ls);
        printResults(ls, "parsed");

         //objetivos y entidades no necesitan de dependencias. se pueden sacar antes de dep.analyse
        // Dependency parser
        dep.analyze(ls);
        printResults(ls, "dep");

        sacar_objetivos(ls);
        eliminar_agentes_repetidos();
        sacar_entidades(ls);

        mn = new ConnSQL();

        try {
            mn.conectar();
            System.out.println("Conexion Satisfactoria!!!");
            disambiguate(ls);
            sacar_operaciones(ls);
            //mn.consultarRegistro("SELECT * FROM persona");
            clasificar();
            mn.desconectar();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> rObj = new ArrayList<String>();
        ArrayList<String> rAgs = new ArrayList<String>();
        ArrayList<String> rOps = new ArrayList<String>();
        ArrayList<String> rEnts = new ArrayList<String>();

        for (int i = 0; i < gls.size(); i++) {
            System.out.println("Objetivo " + (i + 1) + " [" + gls.get(i).getCategory() + "] " + gls.get(i).getDef());
            if (gls.get(i).getCategory().equals("N/A") || gls.get(i).getCategory().equals("")) {
                rObj.add((i + 1) + ". " + gls.get(i).getDef());
            } else {
                rObj.add((i + 1) + ". [" + gls.get(i).getCategory() + "] " + gls.get(i).getDef());
            }
        }
        for (int i = 0; i < ags.size(); i++) {
            System.out.println("Agente " + (i + 1) + " [" + ags.get(i).getCategory() + "] " + ags.get(i).getName() + ". LEma: " + ags.get(i).getLemma());
            if (ags.get(i).getCategory() == null) {
                rAgs.add((i + 1) + ". " + ags.get(i).getName());
            } else {
                rAgs.add((i + 1) + ". [" + ags.get(i).getCategory() + "] " + ags.get(i).getName());
            }
        }
        for (int i = 0; i < ops.size(); i++) {
            System.out.println("Operación " + (i + 1) + " " + ops.get(i).getVerbo() + " " + ops.get(i).getObjeto());
            rOps.add((i + 1) + ". " + ops.get(i).getVerbo() + " " + ops.get(i).getObjeto());
        }
        for (int i = 0; i < ents.size(); i++) {
            System.out.println("Entidad " + (i + 1) + " " + ents.get(i).getName());
            rEnts.add((i + 1) + ". " + ents.get(i).getName());
            for (int j = 0; j < ents.get(i).getAttributes().size(); j++) {
                System.out.println("\t-" + ents.get(i).getAttributes().get(j));
                rEnts.add(" -" + ents.get(i).getAttributes().get(j));
            }
        }

    }

    private static void printSenses(Word w) {
        String ss = w.getSensesString();

        // The senses for a FreeLing word are a list of
        // pair<string,double> (sense and page rank). From java, we
        // have to get them as a string with format
        // sense:rank/sense:rank/sense:rank
        // which will have to be splitted to obtain the info.
        //
        // Here, we just output it:
        System.out.print(" " + ss);
    }

    private static void printResults(ListSentence ls, String format) {
        if (format == "parsed") {
            ////System.out.println("-------- CHUNKER results -----------");

            for (int i = 0; i < ls.size(); i++) {
                TreeNode tree = ls.get(i).getParseTree();
                printParseTree(0, tree);
            }
        } else if (format == "dep") {
            ////System.out.println("-------- DEPENDENCY PARSER results -----------");

            for (int i = 0; i < ls.size(); i++) {
                TreeDepnode deptree = ls.get(i).getDepTree();
                printDepTree(0, deptree, false, false, false, i);
            }
        }
    }

    private static void printParseTree(int depth, TreeNode tr) {
        Word w;
        TreeNode child;
        long nch;

        // Indentation
        for (int i = 0; i < depth; i++) {
            //System.out.print("  ");
        }

        nch = tr.numChildren();

        if (nch == 0) {
            // The node represents a leaf
            if (tr.getInfo().isHead()) {
                //System.out.print("+");
            }
            w = tr.getInfo().getWord();
            //System.out.print(
//                    "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag());
//            printSenses(w);
            ////System.out.println(")");
        } else {
            // The node probably represents a tree
            if (tr.getInfo().isHead()) {
                //System.out.print("+");
            }

            ////System.out.println(tr.getInfo().getLabel() + "_[");
            for (int i = 0; i < nch; i++) {
                child = tr.nthChildRef(i);

                if (child != null) {
                    printParseTree(depth + 1, child);
                } else {
                    System.err.println("ERROR: Unexpected NULL child.");
                }
            }
            for (int i = 0; i < depth; i++) {
                //System.out.print("  ");
            }

            ////System.out.println("]");
        }
    }

    private static void printDepTree(int depth, TreeDepnode tr, boolean b1, boolean b2, boolean b3, int numOracion) {
        TreeDepnode child = null;
        TreeDepnode fchild = null;
        Depnode childnode;
        long nch;
        int last, min;
        Boolean trob;

//        for (int i = 0; i < depth; i++) {
//            System.out.print("  ");
//        }
//        System.out.print(
//                tr.getInfo().getLinkRef().getInfo().getLabel() + "/"
//                + tr.getInfo().getLabel() + "/");
        if (tr.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb") && (tr.getInfo().getWord().getLemma().equals("ser") || tr.getInfo().getWord().getLemma().equals("estar") || tr.getInfo().getWord().getLemma().equals("poseer") || tr.getInfo().getWord().getLemma().equals("tener"))) {
            b3 = true;
        }

        if (tr.getInfo().getLabel().equals("subj")) {
            b1 = true;
        } else if (tr.getInfo().getLabel().equals("dobj")) {
            b1 = false;
            b2 = true;
        }

        if (tr.getInfo().getLabel().equals("co-n") && !b2 && !b3 && !b1) {
//		subjs.add(tr.getInfo().getWord().getForm());
            ags.add(new Agent(tr.getInfo().getWord().getForm(), tr.getInfo().getWord().getLemma(), numOracion));
        }
        if (tr.getInfo().getLabel().equals("subj") && !tr.getInfo().getLinkRef().getInfo().getLabel().equals("coor-n") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn") && !b2 && !b3) {
//		subjs.add(tr.getInfo().getWord().getForm());
            ags.add(new Agent(tr.getInfo().getWord().getForm(), tr.getInfo().getWord().getLemma(), numOracion));
        }

        if (tr.getInfo().getLabel().equals("obj-prep") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn") && b1 && !b3) {
            int totalSbj = ags.size() - 1;
//		subjs.set(totalSbj,subjs.get(totalSbj)+" de "+tr.getInfo().getWord().getForm());
            String prev = ags.get(totalSbj).getName();
            ags.set(totalSbj, new Agent(prev + " de " + tr.getInfo().getWord().getForm(), ags.get(totalSbj).getLemma(), ags.get(totalSbj).getPosicion()));
        }

        if (((tr.getInfo().getLabel().equals("sn-mod") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("w-ms")) || ((tr.getInfo().getLabel().equals("modnomatch") && tr.getInfo().getLinkRef().getInfo().getLabel().equals("sn")))) && b1) {
            int totalSbj = ags.size() - 1;
//		subjs.set(totalSbj,subjs.get(totalSbj)+" "+tr.getInfo().getWord().getForm());
            ags.set(totalSbj, new Agent(ags.get(totalSbj).getName() + " " + tr.getInfo().getWord().getForm(), ags.get(totalSbj).getLemma(), ags.get(totalSbj).getPosicion()));
        }
        if (tr.getInfo().getLabel().equals("adj-mod") && b1 && !b3) {
            int totalSbj = ags.size() - 1;
            //subjs.set(totalSbj,subjs.get(totalSbj)+" "+tr.getInfo().getWord().getForm());
            ags.set(totalSbj, new Agent(ags.get(totalSbj).getName() + " " + tr.getInfo().getWord().getForm(), ags.get(totalSbj).getLemma(), ags.get(totalSbj).getPosicion()));
        }

        Word w = tr.getInfo().getWord();

//        System.out.print(
//                "(" + w.getForm() + " " + w.getLemma() + " " + w.getTag());
//        printSenses(w);
//        System.out.print(")");
        nch = tr.numChildren();

        if (nch > 0) {
//            System.out.println(" [");

            for (int i = 0; i < nch; i++) {
                child = tr.nthChildRef(i);

                if (child != null) {
                    if (!child.getInfo().isChunk()) {
                        printDepTree(depth + 1, child, b1, b2, b3, numOracion);
                    }
                } else {
                    System.err.println("ERROR: Unexpected NULL child.");
                }
            }

            // Print chunks (in order)
            last = 0;
            trob = true;

            // While an unprinted chunk is found, look for the one with lower
            // chunk_ord value.
            while (trob) {
                trob = false;
                min = 9999;

                for (int i = 0; i < nch; i++) {
                    child = tr.nthChildRef(i);
                    childnode = child.getInfo();

                    if (childnode.isChunk()) {
                        if ((childnode.getChunkOrd() > last)
                                && (childnode.getChunkOrd() < min)) {
                            min = childnode.getChunkOrd();
                            fchild = child;
                            trob = true;
                        }
                    }
                }
                if (trob && (child != null)) {
                    printDepTree(depth + 1, fchild, b1, b2, b3, numOracion);
                }

                last = min;
            }

            for (int i = 0; i < depth; i++) {
//                System.out.print("  ");
            }

//            System.out.print("]");
        }

//        System.out.println("");
    }

    /*
    
        
        
    
     */
    public ArrayList<String> getStringGoals() {
        ArrayList<String> rObj = new ArrayList<String>();
//        gls.add(new Goal("Garantizar", "puertas cerradas"));
//        gls.add(new Goal("Promover", "energía de emergencia"));
//        gls.add(new Goal("Alcanzar", "sistema de frenos"));
        for (int i = 0; i < gls.size(); i++) {
            System.out.println("Objetivo " + (i + 1) + " [" + gls.get(i).getCategory() + "] " + gls.get(i).getDef());
            if (gls.get(i).getCategory().equals("N/A") || gls.get(i).getCategory().equals("")) {
                rObj.add(gls.get(i).getDef());
            } else {
                rObj.add("[" + gls.get(i).getCategory() + "] " + gls.get(i).getDef());
            }
        }
        return rObj;
    }

    public ArrayList<String> getStringAgents() {
        ArrayList<String> rAgs = new ArrayList<String>();
//        ags.add(new Agent("Sistema"));
//        ags.add(new Agent("secretaria"));
//        ags.add(new Agent("computador"));
        for (int i = 0; i < ags.size(); i++) {
            System.out.println("Agente " + (i + 1) + " [" + ags.get(i).getCategory() + "] " + ags.get(i).getName() + ". LEma: " + ags.get(i).getLemma());
            if (ags.get(i).getCategory() == null) {
                rAgs.add(ags.get(i).getName());
            } else {
                rAgs.add("[" + ags.get(i).getCategory() + "] " + ags.get(i).getName());
            }
        }
        return rAgs;
    }

    public ArrayList<String> getStringEntities() {
        ArrayList<String> rEnts = new ArrayList<String>();
        ArrayList<String> atts = new ArrayList<String>();
//        atts.add("att1");
//        atts.add("att2");
//        ents.add(new Entity("ambulancia", atts));
//        ents.add(new Entity("formulario"));
        for (int i = 0; i < ents.size(); i++) {
            System.out.println("Entidad " + (i + 1) + " " + ents.get(i).getName());
            rEnts.add(ents.get(i).getName());
            for (int j = 0; j < ents.get(i).getAttributes().size(); j++) {
                System.out.println("\t-" + ents.get(i).getAttributes().get(j));
                rEnts.add(" -" + ents.get(i).getAttributes().get(j));
            }
        }
        return rEnts;
    }

    public ArrayList<String> getStringOperations() {
        ArrayList<String> rOps = new ArrayList<String>();
//        ops.add(new Operation("realizar", "inscripción"));
//        ops.add(new Operation("cotizar", "equipos"));
        for (int i = 0; i < ops.size(); i++) {
            System.out.println("Operación " + (i + 1) + " " + ops.get(i).getVerbo() + " " + ops.get(i).getObjeto());
            rOps.add(ops.get(i).getVerbo() + " " + ops.get(i).getObjeto());
        }

        return rOps;
    }

    public ArrayList<Goal> getGoals() {
        return gls;
    }

    public ArrayList<Agent> getAgents() {
        return ags;
    }

    public ArrayList<Entity> getEntities() {
        return ents;
    }

    public ArrayList<Operation> getOperations() {
        return ops;
    }

    public void sacar_objetivos(ListSentence ls) {

        ////System.out.println("-------- TAGGER results -----------");
        // get the analyzed words out of ls.
        for (int i = 0; i < ls.size(); i++) {
            Sentence s = ls.get(i);

            int aux = -18;
            int forma = 0;
            int comienzo = 0;
            String verbo = "";

            for (int j = 0; j < s.size() - 2; j++) {
                Word w = s.get(j);

                /* NUEVO */
                if (w.getTag().contains("V")) {

                    if (j > 0 && (s.get(j - 1).getTag().contains("N") && (s.get(j + 1).getTag().contains("NC") || s.get(j + 1).getTag().contains("DA")))) {
                        System.out.println("\nEsta frase tiene la forma 2");
                        forma = 2;
                        comienzo = j;
                    }
                    if (j > 0 && (aux == j - 1 || (aux == j - 2 && s.get(j - 1).getTag().contains("CS")))) {
                        if (s.get(j + 1).getTag().contains("DA") && s.get(j + 2).getTag().contains("NCMS")) {
                            System.out.println("\nEsta frase tiene la forma 1");
                            forma = 1;
                            comienzo = j;
                        } else {
                            System.out.println("\nEsta frase tiene la forma 4");
                            forma = 4;
                            comienzo = j;
                        }
                    } else {
                        aux = j;
                        if ((s.get(j + 1).getTag().contains("CS") && (s.get(j + 2).getTag().contains("N") || s.get(j + 2).getTag().contains("DA")) && (s.get(j + 3).getTag().contains("V") || s.get(j + 3).getTag().contains("N")))) {
                            System.out.println("\nEsta frase tiene la forma 3");
                            forma = 3;
                            comienzo = j;
                        }
                    }
                    verbo = w.getLemma();
                    //verbo="spa-30-"+w.getForm()+"-v";

                }
            }
            String line = " ";
            if (forma == 1 || forma == 2 || forma == 3 || forma == 4) {
                for (int k = comienzo; k < s.size(); k++) {
                    if (k == comienzo) {
                        line = line + s.get(k).getLemma();
                    } else {
                        line = line + " " + s.get(k).getForm();
                    }
                }
                String res = grupverb(ls.get(i));
                if (res != null) {
                    gls.add(new Goal(line, res, i, forma));
                }
            }
            /* HASTA AQUI */
            //System.out.print(
//                            w.getForm() + " " + w.getLemma() + " " + w.getTag());
//                    printSenses(w);
            ////System.out.println();
        }

        ////System.out.println();
    }

    public static String grupverb(Sentence s) {
        TreeDepnode tr = s.getDepTree();
        if ((tr.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb") || tr.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb-inf")) && !tr.getInfo().getWord().getLemma().equals("ser")) {
            String vv = tr.getInfo().getWord().getLemma();
            if (!vv.equals("tener") && !vv.equals("ser") && !vv.equals("poseer") && !vv.equals("estar")) {
                return tr.getInfo().getWord().getLemma();
            }
        }
        return null;
    }

    public void eliminar_agentes_repetidos() {
        for (int i = 0; i < ags.size() - 1; i++) {
            for (int j = i + 1; j < ags.size(); j++) {
                if (ags.get(i).getName().equals(ags.get(j).getName())) {
                    ags.remove(j);
                    j--;
                }
            }
        }
    }

    public static void sacar_entidades(ListSentence ls) {
        for (int i = 0; i < ls.size(); i++) {
            Sentence s = ls.get(i);
            int aaaa = 0;
            int auxpos = -1;
            int bandera = 0;
            for (int j = 0; j < s.size() - 2; j++) {
                bandera = 0;
                auxpos = -1;
                aaaa = -1;
                if ((s.get(j).getLemma().equals("de") || s.get(j).getLemma().equals("del")) && (s.get(j + 1).getTag().contains("NC")) && (s.get(j - 1).getTag().contains("NC"))) {
                    if (ents.isEmpty()) {
                        ents.add(new Entity(s.get(j + 1).getLemma(), s.get(j - 1).getLemma()));
                    }
                    for (int ii = 0; ii < ents.size(); ii++) {
                        if (ents.get(ii).getName().equals(s.get(j + 1).getLemma())) {
//                            System.out.println("Entidad repetida: " + ents.get(ii).getName());
                            bandera = 0;
                            auxpos = ii;
                            break;
                        } else {
                            bandera = 1;
                        }
                    }
                    if (bandera == 1) {
//                        System.out.println("Entidad nueva: " + s.get(j + 1).getLemma() + "Atributo: " + s.get(j - 1).getLemma());
                        ents.add(new Entity(s.get(j + 1).getLemma(), s.get(j - 1).getLemma()));
                    } else {

                        for (int ii = 0; ii < ents.get(auxpos).getAttributes().size(); ii++) {
                            if (ents.get(auxpos).getAttributes().get(ii).equals(s.get(j - 1).getLemma())) {
                                aaaa = 0;
                                break;
                            } else {
                                aaaa = 1;
                            }
                        }
                        if (aaaa == 1) {
//                            System.out.println("Entidad repetida: " + s.get(j + 1).getLemma() + "Atributo nuevo: " + s.get(j - 1).getLemma());

                            ents.get(auxpos).addAtt(s.get(j - 1).getLemma());
                        }

                    }

                } else if ((s.get(j).getLemma().equals("de")) && (s.get(j + 2).getTag().contains("NC")) && (s.get(j - 1).getTag().contains("NC"))) {
                    if (ents.isEmpty()) {
                        ents.add(new Entity(s.get(j + 2).getLemma(), s.get(j - 1).getLemma()));
                    }
                    for (int ii = 0; ii < ents.size(); ii++) {
                        if (ents.get(ii).getName().equals(s.get(j + 2).getLemma())) {
//                            System.out.println("Entidad repetida: " + ents.get(ii).getName());
                            bandera = 0;
                            auxpos = ii;
                            break;
                        } else {
                            bandera = 1;
                        }
                    }
                    if (bandera == 1) {
//                        System.out.println("Entidad nueva: " + s.get(j + 2).getLemma() + "Atributo: " + s.get(j - 1).getLemma());
                        ents.add(new Entity(s.get(j + 2).getLemma(), s.get(j - 1).getLemma()));
                    } else {

                        for (int ii = 0; ii < ents.get(auxpos).getAttributes().size(); ii++) {
                            if (ents.get(auxpos).getAttributes().get(ii).equals(s.get(j - 1).getLemma())) {
                                aaaa = 0;
                                break;
                            } else {
                                aaaa = 1;
                            }
                        }
                        if (aaaa == 1) {
//                            System.out.println("Entidad repetida: " + s.get(j + 2).getLemma() + "Atributo nuevo: " + s.get(j - 1).getLemma());

                            ents.get(auxpos).addAtt(s.get(j - 1).getLemma());
                        }

                    }

                }
            }

        }
    }

    public static void sacar_operaciones(ListSentence ls) {
        String asl = "";
        String vbsOp = "go_away.2,exit.1,come.19,go_out.2,go_out.3,depart.6,date.1,fall.28,log_off.1,develop.2,develop.1,develop.7,build_up.5,open.6,clear.1,ease_up.1,move_out.1,take_away.3,draw.13,pull_back.3,recall.6,replace.2,replace.1,exchange.5,create.1,do.3,carry_out.2,effect.2,give.5,lend.2,open.1,open.2,open.4,open.5,unlock.3,open.9,fill.1,shove.3,take_in.12,direct.6,send.4,mail.2,despatch.1,get_off.3,remit.1,message.2,do.2,perform.3,play.6,interpret.3,execute.7,present.4,reassign.1,transfer.2,shift.3,transfer.9,shuffle.2,transfer.5,pass_on.4,call.5,ascertain.2,fasten.1,assure.2,insure.4,brace_up.1,batten.1,disengage.2,free.5,free.11,break_up.5,break.1,discontinue.2,abort.1,look_for.1,record.2,register.1,get_down.6,comb.2,enter.5,look_for.1,record.2,register.1,get_down.6,comb.2,ask.2,bespeak.2,beg.2,allow.4,reserve.3,regenerate.1,resume.3,freshen_up.1,reshape.1,glance_over.1,scan.3,read.4,assay.2,look_for.1,look.4,explore.1,call_for.4,go_after.2,search.4,beat_about.1,hunt.6,carry.28,guard.2,guard.1,gather_in.1,stow.1,put_aside.2,read.3,read.4,decipher.2,appraise.1,do_away_with.1,eliminate.2,abolish.1,bump_off.1,drop.5,remove.2,eliminate.4,wash_away.1,cancel.4,write_off.3,calculate.1,approximate.2,calculate.2,calculate.4,account.2,give.6,give.3,give.4,give.17,devote.2,gift.2,bear.5,apply.6,give_away.1,bestow.2,give.29,process.1,process.6,action.1,process.7,prosecute.1,decide.1,determine.3,determine.2,define.5,ascertain.3,determine.7,calculate.4,judge.1,rubberstamp.2,advertise.2,announce.3,take.1,get_hold_of.1,drink.1,take.21,booze.1,seize.2,appropriate.2,pick_up.9,take_in.1,absorb.6,print.1,bring_out.3,compose.3,write.2,drop_a_line.1,write.7,localise.3,localise.1,constitute.4";
        //String vbsOp = "spa-30-02009433-v,spa-30-02015598-v ,spa-30-02617567-v ,spa-30-01842204-v ,spa-30-02011437-v,spa-30-02066304-v,spa-30-02485844-v ,spa-30-00530177-v ,spa-30-02249293-v ,spa-30-01738597-v ,spa-30-01738774-v ,spa-30-00925873-v ,spa-30-00171852-v ,spa-30-00539936-v ,spa-30-00181664-v ,spa-30-01848465-v ,spa-30-02404904-v ,spa-30-00179311-v ,spa-30-02311387-v ,spa-30-01449053-v ,spa-30-02480216-v ,spa-30-02405390-v ,spa-30-00162688-v ,spa-30-02257767-v ,spa-30-01617192-v ,spa-30-02561995-v ,spa-30-00486018-v ,spa-30-02560767-v ,spa-30-01060494-v ,spa-30-02324182-v ,spa-30-01346003-v ,spa-30-02426171-v ,spa-30-02425462-v ,spa-30-01579813-v ,spa-30-00219963-v ,spa-30-01077887-v ,spa-30-00452512-v ,spa-30-02094569-v ,spa-30-01540844-v ,spa-30-01951480-v ,spa-30-01950798-v ,spa-30-01031256-v ,spa-30-01955127-v ,spa-30-01062555-v ,spa-30-02255081-v ,spa-30-01071474-v ,spa-30-01712704-v ,spa-30-01714208-v ,spa-30-01724459-v ,spa-30-01732172-v ,spa-30-00997659-v ,spa-30-02262752-v ,spa-30-02393086-v ,spa-30-02232190-v ,spa-30-02012344-v ,spa-30-00555240-v ,spa-30-02012973-v ,spa-30-02220461-v ,spa-30-02230247-v ,spa-30-00792471-v ,spa-30-00662589-v ,spa-30-01340439-v ,spa-30-01019643-v ,spa-30-02251065-v ,spa-30-00221718-v ,spa-30-01306425-v ,spa-30-01475953-v ,spa-30-02494047-v ,spa-30-00269682-v ,spa-30-00778275-v ,spa-30-00364064-v ,spa-30-02683840-v ,spa-30-00353839-v ,spa-30-01315613-v ,spa-30-00998399-v ,spa-30-02471690-v ,spa-30-01020356-v ,spa-30-01319193-v ,spa-30-01000214-v ,spa-30-01315613-v ,spa-30-00998399-v ,spa-30-02471690-v ,spa-30-01020356-v ,spa-30-01319193-v ,spa-30-00752493-v ,spa-30-00752764-v ,spa-30-00782057-v ,spa-30-00724150-v ,spa-30-00795632-v ,spa-30-01631072-v ,spa-30-02381951-v ,spa-30-00163441-v ,spa-30-00702065-v ,spa-30-02152278-v ,spa-30-01318659-v ,spa-30-00627520-v ,spa-30-02530167-v ,spa-30-01315613-v ,spa-30-02153709-v ,spa-30-00648224-v ,spa-30-02305586-v ,spa-30-01317533-v ,spa-30-01317723-v ,spa-30-00649362-v ,spa-30-01316401-v ,spa-30-02233195-v ,spa-30-01129337-v ,spa-30-02456031-v ,spa-30-01214786-v ,spa-30-01493234-v ,spa-30-00778885-v ,spa-30-00626428-v ,spa-30-00627520-v ,spa-30-00626130-v ,spa-30-00681429-v ,spa-30-00471711-v ,spa-30-02629256-v ,spa-30-02427334-v ,spa-30-02482425-v ,spa-30-02403920-v ,spa-30-02404224-v ,spa-30-00685419-v ,spa-30-00571273-v ,spa-30-01549187-v ,spa-30-02477655-v ,spa-30-00637259-v ,spa-30-00672433-v ,spa-30-00712135-v ,spa-30-00926472-v ,spa-30-02265231-v ,spa-30-01733477-v ,spa-30-02199590-v ,spa-30-02235842-v ,spa-30-02230772-v ,spa-30-00732224-v ,spa-30-02200686-v ,spa-30-01652139-v ,spa-30-02309165-v ,spa-30-02201855-v ,spa-30-02263692-v ,spa-30-02359553-v ,spa-30-00515154-v ,spa-30-01668603-v ,spa-30-02582042-v ,spa-30-01438681-v ,spa-30-02581900-v ,spa-30-00697589-v ,spa-30-00699815-v ,spa-30-00701040-v ,spa-30-00947077-v ,spa-30-00920336-v ,spa-30-00763399-v ,spa-30-00926472-v ,spa-30-00672277-v ,spa-30-00674517-v ,spa-30-00976653-v ,spa-30-00975427-v ,spa-30-02599636-v ,spa-30-01214265-v ,spa-30-01170052-v ,spa-30-02206619-v ,spa-30-01171183-v ,spa-30-01213614-v ,spa-30-02272549-v ,spa-30-02107248-v ,spa-30-02656995-v ,spa-30-02765464-v ,spa-30-01745722-v ,spa-30-00967625-v ,spa-30-01698271-v ,spa-30-00993014-v ,spa-30-01007027-v ,spa-30-01691057-v ,spa-30-02509919-v ,spa-30-02695895-v ,spa-30-01647229-v";
//        
//         for (int i = 0; i < ags.size(); i++) {
//            for (Map.Entry<String, String> e : palabrasdesamb.entrySet()) {
//                String k = e.getKey();
//                String[] key = k.split("\\.");
//                if (Integer.parseInt(key[0]) == ags.get(i).getPosicion() && key[1].equals(ags.get(i).getLemma())) {
//                    System.out.println("$% " + e.getKey() + " " + e.getValue() + " " + ags.get(i).getLemma());
//                    try {
//                        ags.get(i).setCategory(mn.clasificar_agente(e.getValue()));
//                    } catch (SQLException ex) {
//                    }
//                }
//            }
//        }

        for (int i = 0; i < ls.size(); i++) {
            Sentence s = ls.get(i);
            boolean found = false;
            for (int j = 0; j < s.size() - 1; j++) {
                if ((s.get(j).getTag().contains("V")) && (s.get(j + 1).getTag().contains("NC")) && !(s.get(j).getLemma().contains("ser")) && !(s.get(j).getLemma().contains("tener")) && !(s.get(j).getLemma().contains("haber"))) {
                    asl = "spa-30-" + s.get(j).getSensesString().split(":")[0];
                    System.out.println(" oso " + palabrasdesamb.get(i + "." + s.get(j).getLemma()));
                    if (relacion_operacion(asl)) {
                        ops.add(new Operation(s.get(j).getLemma(), s.get(j + 1).getForm()));
//                        System.out.println("Operation new: " + s.get(j).getLemma() + " " + asl + " " + s.get(j + 1).getLemma());
                    }

                    found = true;
                }
            }
            if (!found) {
                for (int j = 0; j < s.size() - 2; j++) {
                    if ((s.get(j).getTag().contains("V")) && (s.get(j + 2).getTag().contains("NC") && !(s.get(j).getLemma().contains("ser")) && !(s.get(j).getLemma().contains("tener")) && !(s.get(j).getLemma().contains("haber")))) {
                        asl = "spa-30-" + s.get(j).getSensesString().split(":")[0];
                        System.out.println(" oso " + palabrasdesamb.get(i + "." + s.get(j).getLemma()));
                        if (relacion_operacion(asl)) {
//                            System.out.println("Operation new: " + s.get(j).getLemma() + " " + s.get(j + 2).getLemma());
                            ops.add(new Operation(s.get(j).getLemma(), s.get(j + 1).getForm() + " " + s.get(j + 2).getForm()));
                        }
                        found = true;
                    }
                }
            }
            if (!found) {
                for (int j = 0; j < s.size() - 3; j++) {
                    if ((s.get(j).getTag().contains("V")) && (s.get(j + 3).getTag().contains("NC") && !(s.get(j).getLemma().contains("ser")) && !(s.get(j).getLemma().contains("tener")) && !(s.get(j).getLemma().contains("haber")))) {
                        asl = "spa-30" + s.get(j).getSensesString().split(":")[0];
                        System.out.println(" oso " + palabrasdesamb.get(i + "." + s.get(j).getLemma()));
                        if (relacion_operacion(asl)) {
                            ops.add(new Operation(s.get(j).getLemma(), s.get(j + 1).getForm() + " " + s.get(j + 2).getForm() + " " + s.get(j + 3).getForm()));
//                            System.out.println("Operation new: " + s.get(j).getLemma() + " " + s.get(j + 3).getLemma());
                        }
                    }
                }
            }
        }
    }

    public static void disambiguate(ListSentence ls/*, String target*/) throws MalformedURLException, IOException, SQLException {
        writer = new PrintWriter("palabras-desambiguadas.txt", "UTF-8");
        writer.println("PALABRA\t\tSENSE");

        for (int i = 0; i < ls.size(); i++) {//Para cada oracion
            Sentence s = ls.get(i);
            for (int j = 0; j < s.size(); j++) {//Para cada palabra

                if (!stopwords.contains(s.get(j).getLemma())) {
                    if (!s.get(j).getTag().startsWith("F") && !s.get(j).getTag().equals("NP00000") && !s.get(j).getTag().startsWith("Z")) {
                        //si no es stopword:
                        if (!overlap(s.get(j).getLemma(), s, s.get(j).getShortTag(), i, s.get(j).getSensesString().split(":")[0])) {
                            //si no pudo desambiguar con simplifiedlesk

                            String auxiliar = s.get(j).getSensesString().split("/")[0].split(":")[0];
                            writer.println(s.get(j).getLemma() + "\t\tspa-30-" + auxiliar);
                            palabrasdesamb.put(i + "." + s.get(j).getLemma(), "spa-30-" + auxiliar);
                            System.out.println("dos* forma: " + s.get(j).getForm() + " " + s.get(j).getLcForm() + " " + s.get(j).toString());
                            System.out.println("%%%" + s.get(j).getSensesString());
                            //if (deptree.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb") && auxiliar.length() > 0) {
                            int auxn = numOracion(i);
                            if (auxn >= 0 && gls.get(auxn).getVerboP().equals(s.get(j).getLemma())) {

                                sentidoVerboObj(s.get(j).getLemma(), "spa-30-" + auxiliar, auxn, s.get(j).getSensesString().split(":")[0]);
                            }

                        }
                    } else {
                        writer.println(s.get(j).getLemma() + "\t\t--Fin de oración");
                    }
                }

                //deptree=deptree.nthChildRef( i );
            }
        }
        writer.close();
    }

    public static boolean overlap(String lemma, Sentence s, String tipo, int numOracion, String w) throws MalformedURLException, IOException, SQLException {
        try {

//             mn.consultarRegistro("SELECT * FROM persona");
            //defs: lista de todos los sentidos y definiciones para la palabra que entra (lemma)
            ArrayList<String> defsact = new ArrayList<String>();
            try {
                defsact = mn.senseDefinicion(lemma);
                if (defsact.isEmpty()) {
                    throw new Exception();
                }
            } catch (Exception e) {//Si no encontró nada en la base de datos de MCR, busca en la web.

                if (lemma.contains("_")) {
                    return false;
                }
                System.out.println("urll " + lemma + " overlap");
                ArrayList<String> allLines = new ArrayList<String>();
                if (!defs.containsKey(lemma)) {
                    URL oracle;
                    if (tipo.startsWith("V")) {
                        oracle = new URL("http://adimen.si.ehu.es/cgi-bin/wei/public/wei.consult.perl?item=" + lemma + "&button1=Look_up&metode=Word&pos=Verbs&llengua=Spanish_3.0&search=near_synonym&estructura=Spanish_3.0&glos=Gloss&levin=1&spa-30=Spanish_3.0");
                    } else {
                        oracle = new URL("http://adimen.si.ehu.es/cgi-bin/wei/public/wei.consult.perl?item=" + lemma + "&button1=Look_up&metode=Word&pos=Nouns&llengua=Spanish_3.0&search=near_synonym&estructura=Spanish_3.0&glos=Gloss&levin=1&spa-30=Spanish_3.0");
                    }

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    String inputLine;
                    allLines.clear();
                    while ((inputLine = in.readLine()) != null) {
                        allLines.add(inputLine);
                    }
                    in.close();
                    for (int i = 0; i < allLines.size(); i++) {
                        if (allLines.get(i).contains("spa-30-")) {
                            defsact.add(allLines.get(i).substring(3).split("<")[0]);
                        }
                        if (allLines.get(i).contains("<td><td> <font color=#006000")) {
                            String lin[] = allLines.get(i).substring(29).split("<");
                            defsact.set(defsact.size() - 1, defsact.get(defsact.size() - 1) + lin[0]);
                        }
                    }
                    defs.put(lemma, defsact);
                } else {
                    defsact = defs.get(lemma);
                    System.out.println("repetido por aqui " + lemma);
                }
            }
            int cont;
            int[] cDef = new int[defsact.size()];
            for (int i = 0; i < defsact.size(); i++) {

                cont = 0;
                String def[] = defsact.get(i).split(" ");
                for (int k = 0; k < def.length; k++) {
                    for (int j = 0; j < s.size(); j++) {
                        if (def[k].equals(s.get(j).getLemma()) && !stopwords.contains(s.get(j).getLemma())) {
                            cont++;
                        }
                    }
                }
                cDef[i] = cont;
            }
            int max = cDef[0];
            int ind = 0;
            for (int i = 1; i < defsact.size(); i++) {
                if (cDef[i] > max) {
                    max = cDef[i];
                    ind = i;
                }
            }
            if (tipo.startsWith("VM")) {
                ArrayList<String> act = new ArrayList<String>();
                act.add(lemma);
                act.add(defsact.get(ind));
                verbosense.add(act);
            }

            if (max == 0) {
                return false;
            }
            if (gls.get(numOracion).getVerboP().equals(lemma)) {
                System.out.println("uno*" + defsact.get(ind));
                sentidoVerboObj(lemma, defsact.get(ind), numOracion, w);
            }
            System.out.println("Para la palabra::" + lemma + " el sentido es " + defsact.get(ind) + " con fq " + max);

            writer.println(lemma + "\t\t" + defsact.get(ind));
            palabrasdesamb.put(numOracion + "." + lemma, defsact.get(ind).split(" ")[0]);

            //if (tr.getInfo().getLinkRef().getInfo().getLabel().equals("grup-verb") && defs.get(ind).length() > 0) {
            System.out.flush();
            return true;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error " + e.getLocalizedMessage() + e.getMessage());
            return false;
        }
    }

    public static int numOracion(int j) {
        for (int i = 0; i < gls.size(); i++) {
            if (j == gls.get(i).getPosicion()) {
                return i;
            }
        }
        return -1;
    }

    public static void sentidoVerboObj(String lemma, String sens, int numOracion, String w) {//antes de clasificar, necesita haber desambiguado el sentido
        String ans = "";
        _assessor = new SimilarityAssessor();
        try {
//            for (int i = 0; i < verbosense.size(); i++) {
//                System.out.println("verbosense.get(i).get(0)) = " + verbosense.get(i).get(0));
//                if (vbs.contains(verbosense.get(i).get(0))) {
//                    System.out.println("holaas");
//                    System.out.println("verbosense.get(i).get(1).split(\" \")[0] = " + verbosense.get(i).get(1).split(" ")[0]);
//                    ans = verbs(verbosense.get(i).get(1).split(" ")[0]);
//                    clasificaverboobj(ans);
//                }
//            }

            gls.get(numOracion).setCategory(clasificaVerboObj(verbs(sens.split(" ")[0]), lemma, w));

        } catch (Exception e) {
        }

    }

    public static String verbs(String sens) throws MalformedURLException, IOException {
        String ans = "";
        if (sens.equals("spa-30-")) {
            return ans;
        }
        try {
            ans = mn.englishSense(sens);
            if (ans.equals("")) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("urll " + sens + " (verbs)");
            ArrayList<String> allLines = new ArrayList<String>();
            URL oracle = new URL("");
            if (sens.endsWith("v")) {
                oracle = new URL("http://adimen.si.ehu.es/cgi-bin/wei/public/wei.consult.perl?item=" + sens + "&button1=Look_up&metode=Synset&pos=Verbs&llengua=Spanish_3.0&search=near_synonym&estructura=Spanish_3.0&glos=Gloss&levin=1&eng-30=English_3.0&spa-30=Spanish_3.0");
            } else if (sens.endsWith("n")) {
                oracle = new URL("http://adimen.si.ehu.es/cgi-bin/wei/public/wei.consult.perl?item=" + sens + "&button1=Look_up&metode=Synset&pos=Nouns&llengua=Spanish_3.0&search=near_synonym&estructura=Spanish_3.0&glos=Gloss&levin=1&eng-30=English_3.0&spa-30=Spanish_3.0");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            allLines.clear();
            while ((inputLine = in.readLine()) != null) {
                allLines.add(inputLine);
            }
            in.close();
            for (int i = 0; i < allLines.size(); i++) {

                if (allLines.get(i).contains(sens) && i > 40) {//el sense aparece dos veces en la pag web, interesa el segundo, que está despues de la linea 40
                    ans = allLines.get(i - 1).split(" ")[2];
                    //defs.add(spa);
                    ans = ans.replace('_', '.');
                }
            }
            System.out.println("ANSS " + ans + " sens " + sens);
        }
        return ans;

        //_assessor.getSenseSimilarity(DATA, total, DATA, total);
    }

    public static void main(String[] args) {
        try {
            NL2KAOSf1 mn2 = new NL2KAOSf1();
            mn = new ConnSQL();

            try {
                mn.conectar();
            } catch (Exception e) {
            }

            String[] vbsOp = "spa-30-02009433-v ,spa-30-02015598-v ,spa-30-02617567-v ,spa-30-01842204-v ,spa-30-02011437-v ,spa-30-02066304-v ,spa-30-02485844-v ,spa-30-00530177-v ,spa-30-02249293-v ,spa-30-01738597-v ,spa-30-01738774-v ,spa-30-00925873-v ,spa-30-00171852-v ,spa-30-00539936-v ,spa-30-00181664-v ,spa-30-01848465-v ,spa-30-02404904-v ,spa-30-00179311-v ,spa-30-02311387-v ,spa-30-01449053-v ,spa-30-02480216-v ,spa-30-02405390-v ,spa-30-00162688-v ,spa-30-02257767-v ,spa-30-01617192-v ,spa-30-02561995-v ,spa-30-00486018-v ,spa-30-02560767-v ,spa-30-01060494-v ,spa-30-02324182-v ,spa-30-01346003-v ,spa-30-02426171-v ,spa-30-02425462-v ,spa-30-01579813-v ,spa-30-00219963-v ,spa-30-01077887-v ,spa-30-00452512-v ,spa-30-02094569-v ,spa-30-01540844-v ,spa-30-01951480-v ,spa-30-01950798-v ,spa-30-01031256-v ,spa-30-01955127-v ,spa-30-01062555-v ,spa-30-02255081-v ,spa-30-01071474-v ,spa-30-01712704-v ,spa-30-01714208-v ,spa-30-01724459-v ,spa-30-01732172-v ,spa-30-00997659-v ,spa-30-02262752-v ,spa-30-02393086-v ,spa-30-02232190-v ,spa-30-02012344-v ,spa-30-00555240-v ,spa-30-02012973-v ,spa-30-02220461-v ,spa-30-02230247-v ,spa-30-00792471-v ,spa-30-00662589-v ,spa-30-01340439-v ,spa-30-01019643-v ,spa-30-02251065-v ,spa-30-00221718-v ,spa-30-01306425-v ,spa-30-01475953-v ,spa-30-02494047-v ,spa-30-00269682-v ,spa-30-00778275-v ,spa-30-00364064-v ,spa-30-02683840-v ,spa-30-00353839-v ,spa-30-01315613-v ,spa-30-00998399-v ,spa-30-02471690-v ,spa-30-01020356-v ,spa-30-01319193-v ,spa-30-01000214-v ,spa-30-01315613-v ,spa-30-00998399-v ,spa-30-02471690-v ,spa-30-01020356-v ,spa-30-01319193-v ,spa-30-00752493-v ,spa-30-00752764-v ,spa-30-00782057-v ,spa-30-00724150-v ,spa-30-00795632-v ,spa-30-01631072-v ,spa-30-02381951-v ,spa-30-00163441-v ,spa-30-00702065-v ,spa-30-02152278-v ,spa-30-01318659-v ,spa-30-00627520-v ,spa-30-02530167-v ,spa-30-01315613-v ,spa-30-02153709-v ,spa-30-00648224-v ,spa-30-02305586-v ,spa-30-01317533-v ,spa-30-01317723-v ,spa-30-00649362-v ,spa-30-01316401-v ,spa-30-02233195-v ,spa-30-01129337-v ,spa-30-02456031-v ,spa-30-01214786-v ,spa-30-01493234-v ,spa-30-00778885-v ,spa-30-00626428-v ,spa-30-00627520-v ,spa-30-00626130-v ,spa-30-00681429-v ,spa-30-00471711-v ,spa-30-02629256-v ,spa-30-02427334-v ,spa-30-02482425-v ,spa-30-02403920-v ,spa-30-02404224-v ,spa-30-00685419-v ,spa-30-00571273-v ,spa-30-01549187-v ,spa-30-02477655-v ,spa-30-00637259-v ,spa-30-00672433-v ,spa-30-00712135-v ,spa-30-00926472-v ,spa-30-02265231-v ,spa-30-01733477-v ,spa-30-02199590-v ,spa-30-02235842-v ,spa-30-02230772-v ,spa-30-00732224-v ,spa-30-02200686-v ,spa-30-01652139-v ,spa-30-02309165-v ,spa-30-02201855-v ,spa-30-02263692-v ,spa-30-02359553-v ,spa-30-00515154-v ,spa-30-01668603-v ,spa-30-02582042-v ,spa-30-01438681-v ,spa-30-02581900-v ,spa-30-00697589-v ,spa-30-00699815-v ,spa-30-00701040-v ,spa-30-00947077-v ,spa-30-00920336-v ,spa-30-00763399-v ,spa-30-00926472-v ,spa-30-00672277-v ,spa-30-00674517-v ,spa-30-00976653-v ,spa-30-00975427-v ,spa-30-02599636-v ,spa-30-01214265-v ,spa-30-01170052-v ,spa-30-02206619-v ,spa-30-01171183-v ,spa-30-01213614-v ,spa-30-02272549-v ,spa-30-02107248-v ,spa-30-02656995-v ,spa-30-02765464-v ,spa-30-01745722-v ,spa-30-00967625-v ,spa-30-01698271-v ,spa-30-00993014-v ,spa-30-01007027-v ,spa-30-01691057-v ,spa-30-02509919-v ,spa-30-02695895-v ,spa-30-01647229-v".split(" ,");
            for (int i = 0; i < vbsOp.length; i++) {
                System.out.println((vbsOp[i]));
            }
            _assessor = new SimilarityAssessor();
            ArrayList<String> engwords = new ArrayList<String>();
            for (int i = 0; i < vbsOp.length; i++) {
                engwords.add(verbs(vbsOp[i]));
            }
            for (int i = 0; i < engwords.size(); i++) {
                System.out.print(engwords.get(i) + ",");
            }

//            System.out.println("consume.2");
//            System.out.println(clasificaVerboObj("consume.2"));
//            System.out.println("have.6");
//            System.out.println(clasificaVerboObj("have.6"));
//            System.out.println("ingest.1");
//            System.out.println(clasificaVerboObj("ingest.1"));
//            System.out.println("take.18");
//            System.out.println(clasificaVerboObj("take.18"));
//            System.out.println("take_in.15");
//            System.out.println(clasificaVerboObj("take_in.15"));
        } catch (Exception ex) {
        }
    }

    public static String clasificaVerboObj(String sens, String lemma, String w) throws WordNotFoundException {

        System.out.println("clasifico verbooj " + sens);

        double m1 = 0, m2 = 0, m3 = 0, m4 = 0;
        //Verbos de logro
        String[] obj1 = {"develop.1", "promote.1", "better.1", "increase.1", "decrease.2", "minify.1", "confect.2", "produce.2", "advance.2", "make.6", "effect.2", "perform.1", "advance.4", "make.3", "create.1", "make.25", "achieve.1", "reach.2", "negotiate.1", "manage.1", "reach.7", "get.9", "make.21", "have.17", "arrive.1", "get.5", "accomplish.2", "reach.5", "reach.1", "attain.1", "overtake.1", "cause.1", "do.5", "make.5", "induce.2", "cause.2", "do.3", "perform.4", "produce.2", "run.21", "fabricate.1", "construct.2", "form.1", "organize.1", "organize.2", "form.2", "constitute.3", "yield.1"};

        //Verbos de mantener
        String[] obj2 = {"administer.1", "underwrite.1", "subvent.1", "guarantee.2", "ensure.1", "assure.1", "keep.3", "keep.1", "maintain.1", "keep.15", "preserve.5", "achieve.1", "reach.5", "concede.2", "yield.8", "grant.3", "offer.1", "concede.1", "conserve.2", "preserve.2", "continue.3", "preserve.2", "guarantee.1", "guarantee.3", "warrant.2", "guarantee.4", "keep.3", "insure.1", "control.7", "see.10", "promise.2"};

        //Verbos de evitar
        String[] obj3 = {"prevent.2", "defend.2", "exclude.2", "hold.28", "keep_away.1", "blank.1", "impede.1", "hinder.1", "rain_out.1", "avoid.2", "obviate.2", "debar.2", "forefend.1", "forfend.1", "deflect.1", "avoid.1", "hedge.1", "fudge.2", "evade.1", "miss.9", "escape.2", "get_off.5", "get_away.2", "bypass.1", "keep_off.2", "shirk.2", "shy_away_from.1", "shun.1", "eschew.1", "avoid.3", "fiddle.1", "elude.1", "eliminate.4", "avoid.4", "invalidate.1", "break.30", "stet.1", "guard.4"};

        //Verbos de parar
        String[] obj4 = {"discontinue.1", "stop.2", "cease.1", "give_up.4", "drop.7", "leave_off.3", "sign_off.1", "retire.2", "withdraw.2", "pull_the_plug.1", "shut_off.1", "cheese.1", "call_it_quits.1", "break.26", "end.1", "stop.9", "finish.3", "terminate.2", "cease.2", "pass_away.2", "turn_out.4", "conclude.4", "close.5", "vanish.2", "disappear.2", "go.13", "run_out.1", "culminate.1", "vanish.4", "dissapear.3", "recess.3", "go_out.4", "cut_out.6", "lapse.2", "break.23", "stop.1", "go_off.4", "brake.1", "draw_up.5", "stall.5", "stall.2", "rein.2", "check.14", "check.15", "check.5", "pull_up_short.1", "settle.10", "stop.3", "embargo.2", "stay.9", "stop.5", "check.23", "draw_up.3", "stall.6", "stall.7", "flag_down.1", "cut.18", "bring_up.5", "hold.30", "halt.1", "rein.3", "brake.2", "call.13", "lay_over.1", "break.10", "fracture.4", "bog.2", "interrupt.1", "block.12", "cut.36", "block.4", "interject.1", "heckle.2", "burst_upon.1", "break_in.1", "pause.2", "put_away.7", "break.48", "punctuate.3", "take_off.4"};

        System.out.println("similarity de " + sens + " " + sens.split("\\.")[0] + " " + sens.split("\\.")[1] + " " + obj1[0].split("\\.")[0] + " " + Integer.parseInt(obj1[0].split("\\.")[1]));

        String verb = sens.split("\\.")[0];
        int numSense = Integer.parseInt(sens.split("\\.")[1]);
        try {
            double act = _assessor.getSenseSimilarity(verb, numSense, "increase", 1);
        } catch (Exception e) {
            System.out.print("error assesor _" + w);
            try {
                sens = verbs("spa-30-" + w);
            } catch (Exception er) {
            }
            System.out.println(" queda " + sens);
            verb = sens.split("\\.")[0];
            numSense = Integer.parseInt(sens.split("\\.")[1]);
        }

        int cont1 = 0, cont2 = 0, cont3 = 0, cont4 = 0;
        for (int i = 0; i < obj1.length; i++) {
            try {
                double act = 0;
                if (simi == 1) {
                    act = _assessor.getSenseSimilarity(verb, numSense, obj1[i].split("\\.")[0], Integer.parseInt(obj1[i].split("\\.")[1]));// es necesario el doble backslash para separar por puntos
                } else {
                    act = lesk.lesk(verb, numSense, obj4[i].split("\\.")[0], Integer.parseInt(obj4[i].split("\\.")[1]), "v");// es necesario el doble backslash para separar por puntos
                }
                System.out.println((i + 1) + " specific pair 1 = " + act + " " + obj1[i].split("\\.")[0] + "." + Integer.parseInt(obj1[i].split("\\.")[1]));
                if (act > 0) {
                    m1 += act;
                    cont1++;
                }
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < obj2.length; i++) {
            try {
                double act = 0;
                if (simi == 1) {
                    act = _assessor.getSenseSimilarity(verb, numSense, obj2[i].split("\\.")[0], Integer.parseInt(obj2[i].split("\\.")[1]));// es necesario el doble backslash para separar por puntos
                } else {
                    act = lesk.lesk(verb, numSense, obj4[i].split("\\.")[0], Integer.parseInt(obj4[i].split("\\.")[1]), "v");// es necesario el doble backslash para separar por puntos
                }
                System.out.println((i + 1) + " specific pair 2 = " + act + " " + obj2[i].split("\\.")[0] + "." + Integer.parseInt(obj2[i].split("\\.")[1]));
                if (act > 0) {
                    m2 += act;
                    cont2++;
                }
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < obj3.length; i++) {
            try {
                double act = 0;
                if (simi == 1) {
                    act = _assessor.getSenseSimilarity(verb, numSense, obj3[i].split("\\.")[0], Integer.parseInt(obj3[i].split("\\.")[1]));// es necesario el doble backslash para separar por puntos
                } else {
                    act = lesk.lesk(verb, numSense, obj4[i].split("\\.")[0], Integer.parseInt(obj4[i].split("\\.")[1]), "v");// es necesario el doble backslash para separar por puntos
                }
                System.out.println((i + 1) + " specific pair 3 = " + act + " " + obj3[i].split("\\.")[0] + "." + Integer.parseInt(obj3[i].split("\\.")[1]));
                if (act > 0) {
                    m3 += act;
                    cont3++;
                }
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < obj4.length; i++) {
            try {
                double act = 0;
                if (simi == 1) {
                    act = _assessor.getSenseSimilarity(verb, numSense, obj4[i].split("\\.")[0], Integer.parseInt(obj4[i].split("\\.")[1]));// es necesario el doble backslash para separar por puntos
                } else {
                    act = lesk.lesk(verb, numSense, obj4[i].split("\\.")[0], Integer.parseInt(obj4[i].split("\\.")[1]), "v");// es necesario el doble backslash para separar por puntos
                }
                System.out.println((i + 1) + " specific pair 4 = " + act + " " + obj4[i].split("\\.")[0] + "." + Integer.parseInt(obj4[i].split("\\.")[1]));
                if (act > 0) {
                    m4 += act;
                    cont4++;
                }
            } catch (Exception e) {
            }
        }
        System.out.println("m1 = " + m1 + " cont1 = " + cont1 + " m1/cont1 " + m1 / cont1);
        System.out.println("m2 = " + m2 + " cont2 = " + cont2 + " m2/cont2 " + m2 / cont2);
        System.out.println("m3 = " + m3 + " cont3 = " + cont3 + " m3/cont3 " + m3 / cont3);
        System.out.println("m4 = " + m4 + " cont4 = " + cont4 + " m4/cont4 " + m4 / cont4);

        //abajo se pregunta si contx=0, en ese caso se pone 1 para evitar división por cero. sino, se pone contx en el denominador normalmente.
        double[] resultados = {m1 / (cont1 == 0 ? 1 : cont1), m2 / (cont2 == 0 ? 1 : cont2), m3 / (cont3 == 0 ? 1 : cont3), m4 / (cont4 == 0 ? 1 : cont4)};
        int mayor = 0;
        double mayorval = resultados[0];
        for (int i = 1; i < resultados.length; i++) {
            if (resultados[i] > resultados[mayor]) {
                mayor = i;
                mayorval = resultados[i];
            }
        }
        if (mayorval == 0) {
            mayor = 4;
        }
        String ans = "";
        switch (mayor) {
            case 0:
                ans = "Lograr";
                break;
            case 1:
                ans = "Mantener";
                break;
            case 2:
                ans = "Evitar";
                break;
            case 3:
                ans = "Parar";
                break;
            case 4:
                ans = "N/A";
                break;
        }

        //double mayor = Math.max(m1 / cont1, Math.max(m2 / cont2, Math.max(m3 / cont3, m4 / cont4)));
        System.out.println("Para " + lemma + ", mayor = " + ans + " con " + mayorval);
        return ans;
    }

    public void clasificar() {

        for (int i = 0; i < ags.size(); i++) {
            for (Map.Entry<String, String> e : palabrasdesamb.entrySet()) {
                String k = e.getKey();
                String[] key = k.split("\\.");
                if (Integer.parseInt(key[0]) == ags.get(i).getPosicion() && key[1].equals(ags.get(i).getLemma())) {
                    System.out.println("$% " + e.getKey() + " " + e.getValue() + " " + ags.get(i).getLemma());
                    try {
                        ags.get(i).setCategory(mn.clasificar_agente(e.getValue()));
                    } catch (SQLException ex) {
                    }
                }
            }
        }
    }

    public static boolean relacion_operacion(String spa) {
        String[] entra;
        try {
            entra = verbs(spa).split("\\.");
        } catch (Exception ex) {
            return false;
        }
        for (int i = 0; i < vbsOp.length; i++) {
            try {
                double act = 0;
//                double act = lesk.lesk(sens.split("\\.")[0], Integer.parseInt(sens.split("\\.")[1]), obj3[i].split("\\.")[0], Integer.parseInt(obj3[i].split("\\.")[1]), "v");

                String[] actual = vbsOp[i].split("\\.");
                if (simi == 1) {
                    act = _assessor.getSenseSimilarity(entra[0], Integer.parseInt(entra[1]), actual[0], Integer.parseInt(actual[1]));// es necesario el doble backslash para separar por puntos
                } else {
                    act = lesk.lesk(entra[0], Integer.parseInt(entra[1]), actual[0], Integer.parseInt(actual[1]), "v");// es necesario el doble backslash para separar por puntos
                }
                System.out.println((i + 1) + " spec = " + act + " " + spa + " contra " + vbsOp[i]);
                if (act > 0) {
                    return true;
                }
            } catch (Exception e) {
            }

        }
        return false;
    }
}
