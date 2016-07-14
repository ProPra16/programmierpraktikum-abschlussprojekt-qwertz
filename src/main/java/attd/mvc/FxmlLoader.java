package attd.mvc;

import java.io.IOException;
import java.lang.reflect.Field;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FxmlLoader {

	public <Controller, Model> ViewTuple<Controller, Model> load(Class<? extends Controller> controller, Class<? extends Model> model, String name) {

		Controller controllerObject = getNewInstance(controller);
		Model modelObject = injectModel(controllerObject, getNewInstance(model));
		return new ViewTuple<>(controllerObject, modelObject, getParent(controllerObject, name));
	}

	private <T, P> T getNewInstance(Class<? extends P> class1) {
		P p = null;
		try {
			p = class1.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (T) p;
	}

	public <T> Parent getParent(T controller, String name) {
		FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(name));
		loader.setController(controller);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		return parent;
	}

	private <T, H> H injectModel(T viewModel, H model) {
		for (Field field : viewModel.getClass().getDeclaredFields()) {

			if (field.isAnnotationPresent(InjectModel.class) && field.getType().isAssignableFrom(model.getClass())) {
				try {
					boolean bool = field.isAccessible();
					field.setAccessible(true);
					field.set(viewModel, model);
					field.setAccessible(bool);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return model;
	}

}
