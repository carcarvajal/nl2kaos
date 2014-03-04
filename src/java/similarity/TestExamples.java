package similarity;

import java.util.TreeMap;
import java.text.*;



public class TestExamples
{

// 'TestExamples': how to use Java WordNet::Similarity
// David Hope, 2008, University Of Sussex



 	public static void main(String[] args)
	{

// 1. SET UP:

//   Let's make it easy for the user. So, rather than set pointers in 'Environment Variables' etc. let's allow the user to define exactly where they have put WordNet(s)
		String dir = "/home/personal/WordNet-";
//   That is, you may have version 3.0 sitting in the above directory e.g. C:/Program Files/WordNet/3.0/dict
//   The corresponding IC files folder should be in this same directory e.g. C:/Program Files/WordNet/3.0/WordNet-InfoContent-3.0

//   Option 1  (Perl default): specify the version of WordNet you want to use (assuming that you have a copy of it) and use the default IC file [ic-semcor.dat]
		JWS	ws = new JWS(dir, "3.0");
//   Option 2 : specify the version of WordNet you want to use and the particular IC file that you wish to apply
		//JWS ws = new JWS(dir, "3.0", "ic-bnc-resnik-add1.dat");






// 2.3 [ADAPTED LESK MEASURE]
		AdaptedLesk 	lesk = ws.getAdaptedLesk();
							//lesk.useStopList(false);			// by default, the stop list and the lemmatiser
							//lesk.useLemmatiser(false);		// are used to check for 'non content' words in overlaps - here, you can turn these defaults off
		System.out.println("Adapted Lesk (Extended Gloss Overlaps)\n");
// all senses
		TreeMap<String, Double> 	scores3	=	lesk.lesk("apple", "banana", "n");	// all senses
		//TreeMap<String, Double> 	scores3	=	lesk.lesk("apple", 1, "banana", "n"); 		// fixed;all
		//TreeMap<String, Double> 	scores3	=	lesk.lesk("apple", "banana", 2, "n"); 		// all;fixed
		for(String s : scores3.keySet())
			System.out.println(s + "\t" + scores3.get(s));
// specific senses
		System.out.println("\nspecific pair\t=\t" + lesk.lesk("develop", 1, "develop", 2, "v") + "\n");
// max.
		System.out.println("\nhighest score\t=\t" + lesk.max("apple", "banana", "n") + "\n\n\n");


	}
} // eof
