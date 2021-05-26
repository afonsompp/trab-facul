package br.edu.saolucas.utils.huffmacompress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HuffmanTree {

	private HuffmanNode root; // se comporta como uma cifra

	/**
	 * Analisa a frequência de cada caractere e monta um map, onde a chave é o caractere e a frequência
	 * é o valor.
	 */
	private Map<Character, Integer> getCharFrequency(String nonCryptedText) {
		char[] arrayOfChars = nonCryptedText.toCharArray();
		Map<Character, Integer> frequencyMap = new HashMap<>();
		for (char letter : arrayOfChars) {
			if (frequencyMap.get(letter) == null) {
				frequencyMap.put(letter, 1);
			} else {
				frequencyMap.put(letter, frequencyMap.get(letter) + 1);
			}
		}
		return frequencyMap;
	}

	/**
	 * Cria um mapa para os valores binários, onde a chave é o caractere e a string binária é o valor.
	 */
	private Map<Character, String> createBinaryMap(Map<Character, Integer> frequencyMap) {
		Map<Character, String> binaryMap = new HashMap<>(frequencyMap.size());
		for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
			binaryMap.put(entry.getKey(), null);
		}
		return binaryMap;
	}

	/**
	 * Ordena as raízes na análise de Huffman.
	 */
	private List<HuffmanNode> sortRoots(List<HuffmanNode> roots) {
		roots.sort((HuffmanNode o1, HuffmanNode o2) -> o1.getFrequency().compareTo(o2.getFrequency()));
		return roots;
	}

	/**
	 * Inicializa a árvore de Huffman. Monta a lista de raízes.
	 */
	private List<HuffmanNode> initializeTree(Map<Character, Integer> frequencyMap) {
		List<HuffmanNode> roots = new ArrayList<>();
		for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
			HuffmanNode node = new HuffmanNode();
			node.setContent(entry.getKey());
			node.setFrequency(entry.getValue());
			// Marco inicial todos como folha
			node.setIsLeaf(true);
			roots.add(node);
		}
		return sortRoots(roots);
	}

	/**
	 * Cria a árvore de Huffman.
	 */
	private HuffmanNode createTree(Map<Character, Integer> charFrequencyMap) {
		List<HuffmanNode> priorityRootList = this.initializeTree(charFrequencyMap);

		while (priorityRootList.size() > 1) {
			// pega o primeiro e o segundo nó da lista de raízes
			HuffmanNode firstNode = priorityRootList.get(0);
			HuffmanNode secondNode = priorityRootList.get(1);
			// criação do novo nó
			HuffmanNode newRoot = new HuffmanNode();
			// Associa os nós como filhos
			newRoot.setLeft(firstNode);
			newRoot.setRight(secondNode);
			// soma as frequências dos filhos
			newRoot.setFrequency(newRoot.getLeft().getFrequency() + newRoot.getRight().getFrequency());
			// marca o novo nó só para fins de visualização
			newRoot.setContent(666);
			// adiciona na lista
			priorityRootList.add(newRoot);
			// remove os nós antigos
			priorityRootList.remove(firstNode);
			priorityRootList.remove(secondNode);
			// reordena a lista de nós
			this.sortRoots(priorityRootList);
		}
		return priorityRootList.get(0);
	}

	/**
	 * Este método realiza a compressão de todo o texto e utiliza sua versão sobrecarregada para
	 * consultar a árvore. O caractere traço (-) é usado apenas para a visualização da sequencia
	 * binária.
	 *
	 * @param nonCryptedText Representa o texto não comprimido.
	 * @return String Retorna o texto comprimido.
	 */
	public String crypt(String nonCryptedText) {
		char[] arrayCryptedText = nonCryptedText.toCharArray();
		final char SPACE = '-';
		StringBuilder accumulatorCryptedText = new StringBuilder();
		// Criação da tabela de frequência.
		Map<Character, Integer> charFrequencyMap = this.getCharFrequency(nonCryptedText);
		// Criação da tabela binária, relaciona o caractere à sequencia binária
		Map<Character, String> binaryMap = this.createBinaryMap(charFrequencyMap);
		// Cria a árvore de Huffman
		this.root = this.createTree(charFrequencyMap);
		// Chamada ao método recursivo
		this.crypt(this.root, "", binaryMap);

		for (int i = 0; i < arrayCryptedText.length; i++) {
			char nonCrypedLetter = arrayCryptedText[i];
			accumulatorCryptedText.append(binaryMap.get(nonCrypedLetter));
			if (i != arrayCryptedText.length - 1) {
				accumulatorCryptedText.append(SPACE);
			}
		}

		return accumulatorCryptedText.toString();
	}

	/**
	 * Este método de fato realiza a compressão dos dados através de buscas recursivas na árvore de
	 * Huffman.
	 *
	 * @param root Raíz da árvore. É necessário começar deste ponto a busca.
	 * @param currentBinary Representa a atual sequencia binária sendo montada.
	 * @param binaryMap Este mapa é utilizado como um facilitador para o armazenamento das sequencias
	 * binárias. Ele age como a tabela de Huffman.
	 */
	private void crypt(HuffmanNode root, String currentBinary, Map<Character, String> binaryMap) {
		// se é folha é porque cheguei ao caractere
		if (root.isLeaf()) {
			binaryMap.put((char) root.getContent(), currentBinary);
		} else {
			// cada vez que desço à esquerda concateno 0 na sequencia binária.
			crypt(root.getLeft(), currentBinary.concat("0"), binaryMap);
			// cada vez que desço à direita concateno 1 na sequencia binária.
			crypt(root.getRight(), currentBinary.concat("1"), binaryMap);
		}
	}

	/**
	 * Realiza a descompressão.
	 *
	 * @param cryptedText Representa o texto comprimido.
	 * @return String Retorna o texto descomprimido.
	 */
	public String decrypt(String cryptedText) {
		String[] arrayCryptedWords = cryptedText.split("-");
		StringBuilder accumulatorDecryptedText = new StringBuilder();
		for (String cryptedWord : arrayCryptedWords) {
			HuffmanNode currentNode = this.root;
			for (char cryptedLetter : cryptedWord.toCharArray()) {
				// para cada 0 vai para esquerda e para cada 1 vai para a direita
				if (cryptedLetter == '0') {
					currentNode = currentNode.getLeft();
				} else {
					currentNode = currentNode.getRight();
				}
				// se for folha é porque chegou no caractere
				if (currentNode.isLeaf()) {
					accumulatorDecryptedText.append((char) currentNode.getContent());
				}
			}
		}
		return accumulatorDecryptedText.toString();
	}
}
