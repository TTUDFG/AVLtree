import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


//First, we need to consider when we should do self-balanced
//balanced factor = height of the left subtree - height of the right subtree
//When the balance factor is 2 or -2, we need to separate situations to make the balance of the tree
//4 cases to consider when the tree needs to do rotation to be balance
//Imbalance types and rotations: RR, LL ,LR, RL

public class avltree {
    private AVLNode root;
    public AVLNode get_Root() {
        return root;
    }
    static List<Integer> OrderList = new ArrayList<>();

    //Add node on the AVL tree
    public void add(AVLNode node){
        if (root != null){
            root.addNode(node);
        }else {
            root = node;
        }
    }

    //Inorder search to get the number from small to big
    public void Inordersearch(){

        if (root != null){
            root.InordersearchNode();
        } else {
            System.out.println("The tree is empty, cannot perform in order search.");
        }

    }

    //This is used to get the in order list
    public List GetList(){
        List list2;
        list2 = this.OrderList;
        return list2;
    }

    //The function to do search between two number
    public List Searchrange(int small, int big){
        List<Integer> resList = new ArrayList<>();
        this.Inordersearch();
        for(int i = 0; i < this.OrderList.size(); i++){
            if (this.OrderList.get(i) >= small && this.OrderList.get(i)<= big){
                resList.add(this.OrderList.get(i));
            }
        }
        return resList;
    }


    //Search Node
    public AVLNode search(int value){
        if(root==null){
            return null;
        }
        return root.searchNode(value);
    }

    //Search the parent node
    public AVLNode searchParent(int value){
        if (root==null){
            return null;
        }
        return root.searchParentNode(value);
    }

    //FInd the min value of subtree
    public int searchSubtreeMin(AVLNode node){
        AVLNode targetNode = node;
        while(targetNode.leftNode != null){
            targetNode = targetNode.leftNode;
        }
        int minval = targetNode.data;
        Delete(targetNode.data);
        return minval;
    }

    //Delete node on the AVL tree
    public boolean Delete(int value){
        boolean res = true;
        AVLNode targetNode = search(value);
        if (targetNode == null){
            return false;
        }
        AVLNode targetParentNode = searchParent(value);
        if (targetParentNode == null){
            if(root.data != value){
                return false;
            }
        }
        //If the situation of deleting leaf node
        if(targetNode.leftNode == null && targetNode.rightNode==null){
            if(targetParentNode != null){
                if(targetParentNode.leftNode != null && targetParentNode.leftNode.data == value){
                    targetParentNode.leftNode = null;
                }else if (targetParentNode.rightNode != null && targetParentNode.rightNode.data == value){
                    targetParentNode.rightNode = null;
                }
            }else {
                //The parent node is null it is the root
                root = null;
            }
        }else if (targetNode.leftNode != null && targetNode.rightNode != null){
            int min = searchSubtreeMin(targetNode.rightNode);
            targetNode.data = min;
        }else{
            //one degree node deletion
            if(targetNode.leftNode != null){
                if (targetParentNode != null){
                    if (targetParentNode.leftNode.data == value){
                        //if target node is left node of parent
                        targetParentNode.leftNode = targetNode.leftNode;
                    }else if (targetParentNode.rightNode.data == value){
                        //if target node is right node of parent
                        targetParentNode.rightNode = targetNode.leftNode;
                    }
                }else{
                    //If it is deleting the root
                    root = root.leftNode;
                }
            }else{
                //If the node only have right node
                if (targetParentNode != null){
                    if(targetParentNode.rightNode.data == value){
                        //If the target node is the right node
                        targetParentNode.rightNode = targetNode.rightNode;
                    }else if(targetParentNode.leftNode.data == value){
                        targetParentNode.leftNode = targetNode.rightNode;
                    }
                }else{
                    root = root.rightNode;
                }
            }
        }
        return res;
    }

    //Check the height of tree
    public int height(){
        if (root == null){
            return 0;
        }
        return root.getHeight();
    }

    //Check the height of left subtree
    public int leftheight(){
        if (root == null){
            return 0;
        }
        return root.leftHeight();
    }

    //Check the height of right subtree
    public int rightheight(){
        if(root == null){
            return 0;
        }
        return root.rightHeight();
    }

    static class AVLNode {
        int data;
        AVLNode leftNode;
        AVLNode rightNode;


        public AVLNode (int data){
            this.data = data;
        }

        public void addNode(AVLNode Node) {
            //Compare the value in the node first
            if(this.data > Node.data){
                if(this.leftNode==null){
                    //If left node is null, just add the node
                    this.leftNode = Node;
                }else{
                    //If left node is not null, find the leftest one
                    this.leftNode.addNode(Node);
                }

            }else{
                if(this.rightNode==null){
                    //If the right node is null, just add
                    this.rightNode = Node;
                }else{
                    //If right node is not null, find the rightest one
                    this.rightNode.addNode(Node);
                }
            }
            //After adding the node, we need to make judgement of balance factor and rotation
            int bf = balance_factor(leftHeight(),rightHeight());
            if (bf < -1){
                //When we add on the right, we need to judge RL or RR imbalance type
                //It needs the balance factor and height to determine that
                if(rightNode != null && rightNode.leftHeight() > rightNode.rightHeight()){
                    //First, right note rotate right to make it RR
                    rightNode.rightRotate();
                    //Then, rotate left in terms of root
                }
                leftRotate();
            }
            if (bf > 1){
                //The node add on the left side, we need to judge LR or LL imbalance type
                //It needs the height of left and right subtree to determine that
                if(leftNode != null && leftNode.rightHeight() > leftNode.leftHeight()){
                    //First rotate left to become RR
                    leftNode.leftRotate();
                }
                //right rotate for LL
                rightRotate();
            }

        }

        //Check the height of the node
        public int getHeight(){
            return Math.max(leftNode == null ? 0 : leftNode.getHeight(),rightNode == null ? 0 : rightNode.getHeight())+1;
        }

        //Get the height of left subtree
        public int leftHeight(){
            if(leftNode==null){
                return 0;
            }
            return leftNode.getHeight();
        }

        //Get the height of right subtree
        public int rightHeight(){
            if(rightNode==null){
                return 0;
            }

            return rightNode.getHeight();
        }

        public int balance_factor(int left, int right){
            int bf = left - right;
            return bf;
        }
        public void InordersearchNode(){
            if (this.leftNode != null) {
                this.leftNode.InordersearchNode();
            }
            OrderList.add(this.data);
            if(this.rightNode!=null){
                this.rightNode.InordersearchNode();
            }
        }


        public AVLNode searchNode(int data){
            if(this.data == data){
                //System.out.println(this.data);
                return this;
            }else if (this.leftNode != null && this.data > data){
                return this.leftNode.searchNode(data);
            }else if (this.rightNode != null && this.data < data ){
                return this.rightNode.searchNode(data);
            }else{
                return null;
            }
        }

        //Search the parent node
        public AVLNode searchParentNode(int data){
            //If the current node is the expected delete node , return
            if((this.leftNode != null && this.leftNode.data == data) || (this.rightNode != null && this.rightNode.data == data) ){
                return this;
            }else{
                //If the data in the node is small and the left subtree is not empty
                if (data < this.data && this.leftNode != null ){
                    return this.leftNode.searchParentNode(data);//Keep searching left
                } else if(data >= this.data && this.rightNode != null){
                    return this.rightNode.searchParentNode(data);//Keep searching right
                } else{
                    return null; //no parent pointer, already the root of tree
                }
            }
        }

        //Left rotation function
        public void leftRotate(){
            //Create the new node first
            AVLNode node = new AVLNode(data);
            //Make the left node of the new node as the left subtree
            node.leftNode = leftNode;
            //Make the right node of new node as the left subtree of right subtree
            node.rightNode = rightNode.leftNode;
            //Change the value in the new node to the value of right node
            data = rightNode.data;
            //Make the right subtree as the subtree of right subtree
            rightNode = rightNode.rightNode;
            //Make the new node as the left subtree
            leftNode = node;
        }

        //Function for right rotation
        public void rightRotate(){
            //Make a new node first
            AVLNode newNode = new AVLNode(data);
            //Make the new node's right subtree as the right subtree
            newNode.rightNode = rightNode;
            //Make the new node's left subtree as the left subtree of the right subtree
            newNode.leftNode = leftNode.rightNode;
            //Make the value of new node as the left subtree value
            data = leftNode.data;
            //Make the left node's left subtree's left subtree
            leftNode = leftNode.leftNode;
            //Make right subtree as the new node
            rightNode = newNode;
        }

    }

    public static String getKey(String s){
        return s.substring(s.indexOf("(")+1,s.indexOf(")"));
    }

    public static String getCommand(String s){
        return s.substring(0,6);
    }
    public static void main(String[] args) {
//Previous statement for testing
//        AVLTree avlTree = new AVLTree();
//        avlTree.add(new AVLNode(21));
//        avlTree.add(new AVLNode(108));
//        avlTree.add(new AVLNode(5));
//        avlTree.add(new AVLNode(1897));
//        avlTree.add(new AVLNode(4325));
//        avlTree.Delete(108);
//        avlTree.search(1897);
//        avlTree.add(new AVLNode(102));
//        avlTree.add(new AVLNode(65));
//        avlTree.Delete(102);
//        avlTree.Delete(21);
//        avlTree.add(new AVLNode(106));
//        avlTree.add(new AVLNode(23));
//        List list3 = avlTree.Searchrange(23,99);
//        for(int i = 0; i < list3.size(); i++){
//            System.out.println(list3.get(i));
//        }



        //The code to read file
        try {
            if(args.length< 1){
                System.out.println("There is an argument error");
            }
            String filename = args[0];
            StringBuffer sb = new StringBuffer("");
            File myInput = new File(filename);
            FileWriter myWriter = new FileWriter("ouput_file.txt");
            Scanner myScanner = new Scanner(myInput);
            avltree avlTree = null;
            while (myScanner.hasNextLine()) {
                String Commandline = myScanner.nextLine();
                String Command = getCommand(Commandline);
                String Key = getKey(Commandline);

                if (Commandline.equals("Initialize()")) {
                    avlTree = new avltree();
                } else if (Command.equals("Insert")) {
                    int k = Integer.parseInt(Key);
                    avlTree.add(new AVLNode(k));
                } else if (Command.equals("Delete")) {
                    int d = Integer.parseInt(Key);
                    avlTree.Delete(d);
                } else {
                    if(Key.contains(",")){
                        List<String> elementList = Arrays.asList(Key.split(","));
                        int small = Integer.parseInt(elementList.get(0));
                        int big = Integer.parseInt(elementList.get(1));
                        List list2 = avlTree.Searchrange(small,big);
                        for(int i=0; i < list2.size(); i++){
                            sb.append(list2.get(i));
                            sb.append(",");
                        }
                        sb.deleteCharAt(sb.length()-1);
                        sb.append("\n");
                    }else{
                        int s = Integer.parseInt(Key);
//                        avlTree.search(s);
                        if(avlTree.search(s)==null){
                            sb.append("NULL\n");
                        }else{
                            sb.append(avlTree.search(s).data+"\n");
                        }
                    }

                }
//                myScanner.close();

            }
            String printstr = sb.toString();
            myWriter.write(printstr);
            myWriter.close();

        } catch (FileNotFoundException e) {
            System.out.println("The file not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Input and output error");
        }


    }


    
}