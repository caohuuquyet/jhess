package eu.tsp.hess;



	import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



	/**
	 * This is an optimized implementation of the Apriori algorithm that uses binary search to
	 * check if subsets of a candidate are frequent and other optimizations.
	 *  
	 * The apriori algorithm is described  in :
	 * 
	 * Agrawal R, Srikant R. "Fast Algorithms for Mining Association Rules", VLDB.
	 * Sep 12-15 1994, Chile, 487-99,
	 * 
	 * The Apriori algorithm finds all the frequents itemsets and their support in a
	 * binary context.
	 * 
	 * Copyright (c) 2008-2012 Philippe Fournier-Viger
	 * 
	 * This file is part of the SPMF DATA MINING SOFTWARE
	 * (http://www.philippe-fournier-viger.com/spmf).
	 * 
	 * SPMF is free software: you can redistribute it and/or modify it under the
	 * terms of the GNU General Public License as published by the Free Software
	 * Foundation, either version 3 of the License, or (at your option) any later
	 * version.
	 * 
	 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
	 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
	 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * SPMF. If not, see <http://www.gnu.org/licenses/>.
	 */
	public class PatternMining_SaveToFile {

		protected int k; // level

		// stats
		protected int totalCandidateCount = 0;
		protected long startTimestamp;
		protected long endTimestamp;
		private int itemsetCount;
		
		private int minsupRelative;
		
		private List<int[]> database = null;

		// write to file
		BufferedWriter writer = null;
		private double maxMemory;

		public PatternMining_SaveToFile() {
			
		}

		public void runAlgorithm(double minsup, String input, String output, String device) throws IOException {
			startTimestamp = System.currentTimeMillis();
			writer = new BufferedWriter(new FileWriter(output));
			itemsetCount = 0;
			totalCandidateCount = 0;
			maxMemory = 0;

			int transactionCount = 0;
			Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>(); // to count the support of each item
			
			database = new ArrayList<int[]>(); // the database in memory (intially empty)
			
			// scan the database to load it into memory and count the support of each single item at the same time
			BufferedReader reader = new BufferedReader(new FileReader(input));
			String line;
			while (((line = reader.readLine()) != null)) { // for each transaction
				String[] lineSplited = line.split(" ");
				
				int transaction[] = new int[lineSplited.length];
				
				for (int i=0; i< lineSplited.length; i++) { // for each item in the
														// transaction
					// increase the support count of the item
					Integer item = Integer.parseInt(lineSplited[i]);
					transaction[i] = item;
					Integer count = mapItemCount.get(item);
					if (count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
				}
				database.add(transaction);
				transactionCount++;
			}
			reader.close();
			
			this.minsupRelative = (int) Math.ceil(minsup * transactionCount);
			
			k = 1;
			
			// all frequent items are added to the set of candidate
			List<Integer> frequent1 = new ArrayList<Integer>();
			for(Entry<Integer, Integer> entry : mapItemCount.entrySet()){
				if(entry.getValue() >= minsupRelative){
					frequent1.add(entry.getKey());
					saveItemsetToFile(entry.getKey(), entry.getValue(),transactionCount); 
				}
			}
			mapItemCount = null;
			
			// sort the list of candidates by lexical order
			Collections.sort(frequent1, new Comparator<Integer>() {
				public int compare(Integer o1, Integer o2) {
					return o1 - o2;
				}
			});
			
			// if no frequent item, we stop there!
			if(frequent1.size() == 0){
				return;
			}
			
			totalCandidateCount += frequent1.size();
			
			List<Itemset> level = null;
			k = 2;
			// While the level is not empty
			do{
				checkMemory();
				// Generate candidates of size K
				List<Itemset> candidatesK;
				
				if(k ==2){
					candidatesK = generateCandidate2(frequent1);
				}else{
					candidatesK = generateCandidateSizeK(level);
				}
				
				totalCandidateCount += candidatesK.size();

				// We scan the database one time to calculate the support
				// of each candidates and keep those with higher suport.
				for(int[] transaction: database){
					
		 loopCand:	for(Itemset candidate : candidatesK){
						int pos = 0;
						for(int item: transaction){
							if(item == candidate.items[pos]){
								pos++;
								if(pos == candidate.items.length){
									candidate.count++;
									continue loopCand;
								}
							}else if(item > candidate.items[pos]){
								continue loopCand;
							}
							
						}
					}
				}

				// We build the level k+1 with all the candidates that have
				// a support higher than the minsup threshold.
				level = new ArrayList<Itemset>();
				for (Itemset candidate : candidatesK) {
					if (candidate.getAbsoluteSupport() >= minsupRelative) {
						level.add(candidate);
						saveItemsetToFile(candidate,transactionCount);
					}
				}
				
				k++;
			}while(level.isEmpty() == false);

			endTimestamp = System.currentTimeMillis();
			checkMemory();

			writer.close();
		}

		private List<Itemset> generateCandidate2(List<Integer> frequent1) {
			List<Itemset> candidates = new ArrayList<Itemset>();
			
			// For each itemset I1 and I2 of level k-1
			for (int i = 0; i < frequent1.size(); i++) {
				Integer item1 = frequent1.get(i);
				for (int j = i + 1; j < frequent1.size(); j++) {
					Integer item2 = frequent1.get(j);

					// Create a new candidate by combining itemset1 and itemset2
					candidates.add(new Itemset(new int []{item1, item2}));
				}
			}
			return candidates;
		}

		protected List<Itemset> generateCandidateSizeK(List<Itemset> levelK_1) {
			List<Itemset> candidates = new ArrayList<Itemset>();

			// For each itemset I1 and I2 of level k-1
			loop1: for (int i = 0; i < levelK_1.size(); i++) {
				int[] itemset1 = levelK_1.get(i).items;
				loop2: for (int j = i + 1; j < levelK_1.size(); j++) {
					int[] itemset2 = levelK_1.get(j).items;

					// we compare items of itemset1 and itemset2.
					// If they have all the same k-1 items and the last item of
					// itemset1 is smaller than
					// the last item of itemset2, we will combine them to generate a
					// candidate
					for (int k = 0; k < itemset1.length; k++) {
						// if they are the last items
						if (k == itemset1.length - 1) {
							// the one from itemset1 should be smaller (lexical
							// order)
							// and different from the one of itemset2
							if (itemset1[k] >= itemset2[k]) {
								continue loop1;
							}
						}
						// if they are not the last items, and
						else if (itemset1[k] < itemset2[k]) {
							continue loop2; // we continue searching
						} else if (itemset1[k] > itemset2[k]) {
							continue loop1; // we stop searching: because of lexical
											// order
						}
					}

					// Create a new candidate by combining itemset1 and itemset2
					int newItemset[] = new int[itemset1.length+1];
					System.arraycopy(itemset1, 0, newItemset, 0, itemset1.length);
					newItemset[itemset1.length] = itemset2[itemset2.length -1];

					// The candidate is tested to see if its subsets of size k-1 are
					// included in
					// level k-1 (they are frequent).
					if (allSubsetsOfSizeK_1AreFrequent(newItemset, levelK_1)) {
						candidates.add(new Itemset(newItemset));
					}
				}
			}
			return candidates;
		}

		protected boolean allSubsetsOfSizeK_1AreFrequent(int[] candidate, List<Itemset> levelK_1) {
			// generate all subsets by always each item from the candidate, one by one
			for(int posRemoved=0; posRemoved< candidate.length; posRemoved++){

				// perform a binary search to check if  the subset appears in  level k-1.
		        int first = 0;
		        int last = levelK_1.size() - 1;
		       
		        boolean found = false;
		        // the binary search
		        while( first <= last )
		        {
		        	int middle = ( first + last ) / 2;

		            if(sameAs(levelK_1.get(middle), candidate, posRemoved)  < 0 ){
		            	first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
		            }
		            else if(sameAs(levelK_1.get(middle), candidate, posRemoved)  > 0 ){
		            	last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
		            }
		            else{
		            	found = true; //  we have found it so we stop
		                break;
		            }
		        }

				if(found == false){  // if we did not find it, that means that candidate is not a frequent itemset because
					// at least one of its subsets does not appear in level k-1.
					return false;
				}
			}
			return true;
		}

		
	private int sameAs(Itemset itemset, int [] candidate, int posRemoved) {
			int j=0;
			for(int i=0; i<itemset.items.length; i++){
				if(j == posRemoved){
					j++;
				}
				if(itemset.items[i] == candidate[j]){
					j++;
				}else if (itemset.items[i] > candidate[j]){
					return 1;
				}else{
					return -1;
				}
			}
			return 0;
		}
		
		public void saveItemsetToFile(Itemset itemset, int transactionCount) throws IOException {
			
			writer.write(itemset.toString() + " support: "
					+ (itemset.getAbsoluteSupport()/(1.0*transactionCount) )*100 +"%" );
			writer.newLine();
			itemsetCount++;
		}
		
		public void saveItemsetToFile(Integer item, Integer support, int transactionCount) throws IOException {
			writer.write(item/100 + "h"+ (item%100!=0?item%100+"":"") + " support: " + (support/(1.0*transactionCount) *100) +"%");
			writer.newLine();
			itemsetCount++;
		}
		
		private void checkMemory() {
			double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
					/ 1024d / 1024d;
			if (currentMemory > maxMemory) {
				maxMemory = currentMemory;
			}
		}

		public void printStats() {
			System.out.println("=============  APRIORI - STATS =============");
			System.out.println(" Candidates count : " + totalCandidateCount);
			System.out.println(" The algorithm stopped at size " + (k - 1)
					+ ", because there is no candidate");
			System.out.println(" Frequent itemsets count : " + itemsetCount);
			System.out.println(" Maximum memory usage : " + maxMemory + " mb");
			System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
			System.out.println("===================================================");
		}
	}

