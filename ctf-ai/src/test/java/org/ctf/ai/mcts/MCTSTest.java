package org.ctf.ai.mcts;

import static org.junit.jupiter.api.Assertions.*;

import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MCTSTest {
	static MCTS mcts;
	static GameState gameState;

	@BeforeEach
	void setUp() {
		gameState = TestValues.getTestState();
		TreeNode parent = new TreeNode(null, gameState, 0, new int[] {0,0});
		mcts = new MCTS(parent);
	}
	
	@Test
	void testGetState() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectAndExpand() {
		fail("Not yet implemented");
	}

	@Test
	void testExpand() {
		fail("Not yet implemented");
	}

	@Test
	void testSimulate() {
		fail("Not yet implemented");
	}

	@Test
	void testTerminalHeuristic() {
		fail("Not yet implemented");
	}

	@Test
	void testPickField() {
		fail("Not yet implemented");
	}

	@Test
	void testBackpropagate() {
		fail("Not yet implemented");
	}

	@Test
	void testIsTerminal() {
		fail("Not yet implemented");
	}

	@Test
	void testIsFullyExpanded() {
		fail("Not yet implemented");
	}

	@Test
	void testBestChild() {
		mcts.root.children = new TreeNode[2];
		mcts.root.children[0] = new TreeNode(null, gameState, 1, new int[] {0,0});
		mcts.root.children[0].parent = mcts.root;
		mcts.root.children[1] = new TreeNode(null, gameState, 1, new int[] {0,0});
		mcts.root.children[1].parent = mcts.root;										//2 Kindknoten als Kinder von root initialisiert

		mcts.root.children[0].wins = new int[] {4, 0};									//Team 0 hat mehr wins als Team 1
		mcts.root.children[1].wins = new int[] {0, 12};									//Team 1 hat mehr wins als Team 0
		mcts.root.wins = new int[] {4, 12};
		TreeNode bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
		assertEquals(bestChild, mcts.root.children[1]);									//Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser
		
		mcts.root.gameState.setCurrentTeam(0);
		bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
		assertEquals(bestChild, mcts.root.children[0]);									//Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
	}

	@Test
	void testGetRootBest() {
		mcts.root.children = new TreeNode[2];
		mcts.root.children[0] = new TreeNode(null, gameState, 1, new int[] {0,0});
		mcts.root.children[0].parent = mcts.root;
		mcts.root.children[1] = new TreeNode(null, gameState, 1, new int[] {0,0});
		mcts.root.children[1].parent = mcts.root;										//2 Kindknoten als Kinder von root initialisiert

		mcts.root.children[0].wins = new int[] {4, 0};									//Team 0 hat mehr wins als Team 1
		mcts.root.children[1].wins = new int[] {0, 12};									//Team 1 hat mehr wins als Team 0
		mcts.root.wins = new int[] {4, 12};
		TreeNode bestChild = mcts.getRootBest(mcts.root);
		assertEquals(bestChild, mcts.root.children[1]);									//Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser
		
		mcts.root.gameState.setCurrentTeam(0);
		bestChild = mcts.getRootBest(mcts.root);
		assertEquals(bestChild, mcts.root.children[0]);									//Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
	}

	@Test
	void testOneMove() {
		fail("Not yet implemented");
	}

	@Test
	void testPrintResults() {
		fail("Not yet implemented");
	}

}
