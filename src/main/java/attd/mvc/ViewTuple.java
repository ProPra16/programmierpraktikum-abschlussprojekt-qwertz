package attd.mvc;

import javafx.scene.Parent;

public class ViewTuple<Controller, Model> {
	private Controller controller;
	private Model model;
	private Parent parent;

	public ViewTuple(Controller controller, Model model, Parent parent) {
		this.controller = controller;
		this.parent = parent;
		this.model = model;
	}

	public Controller getView() {
		return controller;
	}

	public Parent getParent() {
		return parent;
	}

	public Model getModel() {
		return model;
	}
}
