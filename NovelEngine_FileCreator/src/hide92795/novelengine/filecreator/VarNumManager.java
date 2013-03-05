package hide92795.novelengine.filecreator;

import java.util.HashMap;

public enum VarNumManager {
	IMAGE, SOUND, CLICKABLE, BUTTON, BUTTON_POSITION, CHAPTER_ID, SCENE_ID, CHARACTER, CHARACTER_POSITION, FACE_TYPE;
	private int num;

	private HashMap<String, Integer> varMap = new HashMap<String, Integer>();

	public int add(String name) {
		if (!varMap.containsKey(name)) {
			int id = nextNum();
			varMap.put(name, id);
			return id;
		} else {
			return varMap.get(name);
		}
	}

	private int nextNum() {
		num++;
		return this.num;
	}

	public HashMap<String, Integer> getMap() {
		return varMap;
	}

	public void reset() {
		varMap.clear();
	}
}
