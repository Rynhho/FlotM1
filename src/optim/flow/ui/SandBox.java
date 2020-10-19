package optim.flow.ui;

import optim.flow.domain.Network;

import optim.flow.infra.Parser;

public class SandBox {

	public static void main(String[] args) {
		double[][] b = { { 1, 1 }, { 1, 1 } };
		double[][] c = { { 1, 0 }, { 0, 1 } };
		double[] d = { -1, 1 };
		Network net = new Network(10, 80, 30, 30, 20);
		// new Network(b, c, d);
		System.out.println(net);
		System.out.println(net.checkValidity());
		net.save("salut");
		Parser parse = new Parser();
		Network lala = parse.loadFromFile("salut");

		System.out.println(lala.toString());
		System.out.println("done");
	}

}
