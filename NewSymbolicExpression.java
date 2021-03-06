/* package codechef; // don't place package name! */

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

class NewSymbolicExpression{
  public static void main(String[] args){
    Scanner scnr = new Scanner(System.in);
    String input = scnr.nextLine();
    scnr.close();
    validateInput(input);
    
    String print = getExpression(input);
    System.out.println(print);
  }
  
  //check for E1 -> bad input
  //valid input is in form "(String,String) (String,String) ..." where each String can contain only uppercase           
  //letters, lowercase letters, and numbers
  public static void validateInput(String input){
    //^\(([a-zA-Z0-9]*[,][A-Za-z0-9]*)\)$ http://www.regexplanet.com/advanced/java/index.html
    String regex = "^\\(([a-zA-Z0-9]*[,][A-Za-z0-9]*)\\)$"; 
    Pattern p = Pattern.compile(regex);
    
    //split input string on whitespace and check to see if each element matches our regex
    String[] arr = input.split("\\s+"); 
    for (String test : arr){
        Matcher m = p.matcher(test);
        if (!m.matches()){
            System.out.println("E1");
            System.exit(0);
      }
    }
  }
  
  public static String getExpression(String input){
    //allNodes holds all the nodes in the tree    
    Set<String> allNodes = new HashSet<String>();
    //allChildren only holds nodes that are children of some other node
    Set<String> onlyChildren = new HashSet<String>();
    //holds relationships between parent nodes and their children
    Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
    
    //checks for E2 -> duplicate edge
    String[] arr = input.split("\\s+");
    for (String temp : arr){
        String[] temparr = temp.split(",");
        //remove the first parantheses "(" for parent and second parantheses ")" for child 
        String parent = temparr[0].substring(1);
        String child = temparr[1].substring(0,temparr[1].length()-1);
        
        ArrayList<String> templist = new ArrayList<String>();
        if (map.get(parent) != null){
            //check if the parent-child relationship already exists in the map
            templist = map.get(parent);
            for (String str : templist){
                if (child.equals(str)) return "E2";
            }   
        }
        templist.add(child);
        map.put(parent, templist);
        allNodes.add(parent);
        allNodes.add(child);
        onlyChildren.add(child);
    }
                    
    //check error E3 -> >2 children
    for (String parent : map.keySet()){
      int numChild = map.get(parent).size();
      if (numChild > 2){
          return "E3";
      }
    }
    
    //check E4 -> multiple roots
    //note that root won't have any parents 
    allNodes.removeAll(onlyChildren); //set difference operation
    if (allNodes.size() > 1){
        return "E4";
    }
    
    //if there is no root then there must be cycle
    if (allNodes.size() == 0){
        return "E5";
    }
    
    //check for a cycle using the isCycle helper method
    String root = allNodes.iterator().next();
    Set<String> visited = new HashSet<String>();
    if (isCycle(root, map, visited)){
        return "E5";
    }
     
    //there are no errors so use the helper method to construct the actual S expression   
    return helper(root, map);
  }
  
    //recursive method that starts with the root and repeats with all children of given node
  //if a node has been visited before then there is a cycle
  private static boolean isCycle(String parent, Map<String,ArrayList<String>> map, Set<String> visited){
      if (visited.contains(parent)) return true;
      visited.add(parent);
      if (map.get(parent) != null){
          for (String kid : map.get(parent)){
              if (isCycle(kid, map, visited)) return true;
          }
     }
     return false;
  }

  //recursive DFS method to construct the S expression
  private static String helper(String parent, Map<String,ArrayList<String>> map){
    String leftChild = "";
    String rightChild = "";
    
    if (map.get(parent) != null){
        ArrayList<String> alist = map.get(parent);
        Collections.sort(alist);//sort to get lexographically smallest
        leftChild = helper(alist.get(0), map);
        if (alist.size() > 1){
            rightChild = helper(alist.get(1), map);
        }
    }
    
    return "("+parent+leftChild+rightChild+")";
  }
}
