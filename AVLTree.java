import java.util.Scanner;

// cada nó (node) guarda um valor/inteiro, referências para os filhos esquerdo e direito, e a altura do nó para calcular o fator de balanceamento
class Node {
    int key, height; 
    Node left, right;

    Node(int d) {
        key = d;
        height = 1;
    }
}

public class AVLTree{
    Node root;

    // obter altura do nó
    int height(Node N) {
        return (N == null) ? 0 : N.height;
    }

    // obter o maior entre os dois inteiros
    int max(int a, int b){
        return (a > b) ? a : b;
    }

    // Left-Left - Rotação a direita
    Node rightRotate(Node y){
        Node x = y.left;
        Node T2 = x.right;

        // realiza rotação
        x.right = y;
        y.left = T2;

        // atualiza as alturas
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x; // nova raiz
    }

    // Right-Right - rotação a esquerda
    Node leftRotate(Node x){
        Node y = x.right;
        Node T2 = y.left;

        // realiza a rotação
        y.left = x;
        x.right = T2;

        //Atualiza as alturas
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y; // nova raiz
    }

    // obtem o fator de balanceamento do nó
    int getBalance(Node N){
        return (N == null) ? 0 : height(N.left) - height(N.right);
    }


/// metodo de inserção - i
    Node insert(Node node, int key){
        
        // realiza a inserção
        if (node == null) return (new Node(key));

        if (key < node.key)
            node.left = insert(node.left, key);

        else if (key > node.key)
            node.right = insert(node.right, key);
        else
            return node;

        // atualiza a altura do nó ancestral
        node.height = 1 + max(height(node.left), height(node.right));

        // obtem o fator de balanceamento para verificar se ficou desequilibrado
        int balance = getBalance(node);

        if (balance > 1) {
            if (key < node.left.key){
                return rightRotate(node); // caso LL
            } else {
                node.left = leftRotate(node.left); // caso LR  
                return rightRotate(node);
            }
        }

        if (balance < -1){
            if (key > node.right.key){
                return leftRotate(node); // caso RR
            } else {
                node.right = rightRotate(node.right); // caso RL
                return leftRotate(node);
            }
        }

        /*
        // se o nó ficar desequilibrado, temos 4 casos:
        
        // 1 - left subtree too heavy (Left-left, LL)
        if (balance > 1 && key < node.left.key){
            return rightRotate(node);
        }

        // 2 - right subtree too heavy (Right-right, RR)
        if (balance < -1 && key > node.left.key){
            return rightRotate(node);
        }

        // 3 - node inserted into the right subtree of a left child (Left-right, LR)
        if (balance > 1 && key > node.left.key){
            node.left = leftRotate(node.left); // Left Rotation on the left child
            return rightRotate(node); // Right Rotation on the parent node
        }

        // 4 - node inserted into the left subtree of a right child (Right -left, RL)
        if (balance < -1 && key < node.right.key){
            node.right = rightRotate(node.right); // Right Rotation on the right child
            return leftRotate(node); // Left Rotation on the parent node
        }
    */
        return node;
    }

    // função para encontrar o sucessor imediato: da um passo para a subarvore direita e desce o máximo da arvore esquerda. O último nó sera o menor valor pq os menores valores estão sempre a esquerda
    Node minValueNode(Node node){
        Node current = node;
        // percorre a arvore para encontrar a node mais a esquerda
        while (current.left != null){
            current = current.left;
        }
        return current;
    }

/// metodo de remoção - r
    Node deleteNode(Node root, int key){
        // remoção padrão
        if (root == null) return root;
        if (key < root.key){
            root.left = deleteNode(root.left, key);
        } else if (key > root.key){
            root.right = deleteNode(root.right, key);
        } else {
            // Nó encontrado
            if ((root.left == null) || (root.right == null)){
                Node temp = (root.left != null) ? root.left : root.right;
                // caso sem filhos -> só remover
                if (temp == null){
                    temp = root;
                    root = null;
                } 
                // caso com filhos -> filho assume o lugar
                else {
                    root = temp;
                }
            }
            // caso com dois filhos -> procurar sucessor -> copia o valor dele -> remove o sucessor original
            else {
                Node temp = minValueNode(root.right);
                root.key = temp.key;
                root.right = deleteNode(root.right, temp.key);
            }
        }

        if (root == null) return root;

        // atualizar altura do nó
        root.height = max(height(root.left), height(root.right)) + 1;

        // rebalancear
        int balance = getBalance(root);

        // LL 
        if (balance > 1 && getBalance(root.left) >= 0){
            return rightRotate(root);
        }

        // LR
        if (balance > 1 && getBalance(root.left) < 0){
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // RR
        if (balance < -1 && getBalance(root.right) <= 0){
            return leftRotate(root);
        }

        // RL
        if (balance < -1 && getBalance(root.right) > 0){
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    
    }    


/// método de busca - b
    public void search(int key){
        System.out.print("Caminho percorrido: ");
        boolean found = searchRecursive(root, key);
        if (found){
            System.out.println("\n-> Elemento " + key + " encontrado na arvore.");        
        } else {
            System.out.println("\n-> Elemento " + key + " nao encontrado na arvore."); 
        }
    }

    private boolean searchRecursive(Node current, int key){
        if (current == null) return false;

        // imprime o nó atual como parte do caminho
        System.out.println(current.key + " ");

        if (key == current.key) return true;

        // decide para qual lado ir
        if (key < current.key) return searchRecursive(current.left, key);
        return searchRecursive(current.right, key); // else
    }   

/// caminhamento
    // pre-ordem: raiz -> esquerda -> direita (copiar arvore)
    void preOrder(Node node){
        if (node != null){
            System.out.println(node.key + " ");
            preOrder(node.left);
            preOrder(node.right);
        }
    }

    // pos-ordem: esquerda -> direita -> raiz (deletar árvore ou avaliar expressões matematicas)
    void postOrder(Node node){
        if (node != null){
            postOrder(node.left);
            postOrder(node.right);
            System.out.println(node.key + " ");
        }
    }

    //em-ordem: esquerda -> raiz -> direita (copiar a árvore)
    void inOrder(Node node){
        if (node != null){
            inOrder(node.left);
            System.out.println(node.key + " ");
            inOrder(node.right);
        }
    }

    void prettyPrintTree(Node node, int level) {
        if (node == null) return;
        
        prettyPrintTree(node.right, level + 1);
        
        for (int i = 0; i < level; i++) {
            System.out.print("    "); // 
        }
        System.out.println(node.key);
        
        prettyPrintTree(node.left, level + 1);
    }

    public void printTree() {
        if (root == null) {
            System.out.println("(Arvore vazia)");
            return;
        }
        prettyPrintTree(root, 0);
    }
    
    /* 
    void printTree (Node node, String indent, boolean last){
        if (node != null){
            System.out.println(indent);
            if (last){
                System.out.println("R---");
                indent += "   ";
            } else {
                System.out.println("L---");
                indent += "/   ";
            }
            System.out.println(node.key + " (H:" + node.height + ")");
            printTree(node.left, indent, false);
            printTree(node.right, indent, true);
        }
    }
    */

    public static void main (String[] args){
        AVLTree tree = new AVLTree();
        Scanner sc = new Scanner(System.in); 
        System.out.println("--------------------------------------");
        System.out.println("Arvore AVL Iniciada. Comandos: i X, r X, b X, pre, em, pos, sair.");

        // laço le os comandos palavra por palavra até o usuario digitar 'sair'
        while (sc.hasNext()) {
            String command = sc.next();

            if (command.equals("sair")) {
                break;
            }

            if (command.equals("i")) {
                int val = sc.nextInt(); // Lê o próximo inteiro
                tree.root = tree.insert(tree.root, val);
                System.out.println("    ");
                System.out.println("Arvore apos insercao: ");
                tree.printTree();
                System.out.println("---------------------");

            }
            else if (command.equals("r")) {
                int val = sc.nextInt(); 
                tree.root = tree.deleteNode(tree.root, val);
                System.out.println("    ");
                System.out.println("Arvore apos remocao: ");
                tree.printTree();
                System.out.println("---------------------");

            }
            else if (command.equals("b")) {
                int val = sc.nextInt(); 
                tree.search(val);
            }
            else if (command.equals("pre")) {
                System.out.println("      ");
                System.out.print("Pre-ordem: ");
                tree.preOrder(tree.root);
                System.out.println();
                System.out.println("---------------------");

            }
            else if (command.equals("em")) {
                System.out.println("      ");
                System.out.print("Em-ordem: ");
                tree.inOrder(tree.root);
                System.out.println();
                System.out.println("---------------------");
            }
            else if (command.equals("pos")) {
                System.out.println("      ");
                System.out.print("Pos-Ordem: ");
                tree.postOrder(tree.root);
                System.out.println();
                System.out.println("---------------------");

            }
        }

        sc.close();
    }
}