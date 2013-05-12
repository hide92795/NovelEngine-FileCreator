//
// NovelEngine Project
//
// Copyright (C) 2013 - hide92795
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package hide92795.novelengine.filecreator;

import java.util.HashMap;

/**
 * リソースのIDに重複がないように番号を提供するマネージャーです。
 * 
 * @author hide92795
 */
public enum VarNumManager {
	/**
	 * 各種リソースのIDを管理するマネージャーの列挙オブジェクトです。
	 */
	IMAGE, SOUND, CLICKABLE, BUTTON, BUTTON_POSITION, CHAPTER_ID, SCENE_ID, CHARACTER, CHARACTER_POSITION, FACE_TYPE, FONT, FONT_NAME, VOICE, FIGURE;
	/**
	 * 次に提供するIDです。
	 */
	private int id;
	/**
	 * 既に提供したIDを管理するためのマップです。
	 */
	private HashMap<String, Integer> varMap = new HashMap<String, Integer>();

	/**
	 * リソースに対してIDを割り振ります。<br>
	 * 指定した名前のリソースが既に登録されている場合はそのIDを返します。
	 * 
	 * @param name
	 *            リソースの名前
	 * @return リソースのID
	 */
	public int add(String name) {
		if (!varMap.containsKey(name)) {
			int id = nextNum();
			varMap.put(name, id);
			return id;
		} else {
			return varMap.get(name);
		}
	}

	/**
	 * 次のIDを返します。
	 * 
	 * @return 次のID
	 */
	private int nextNum() {
		id++;
		return this.id;
	}

	/**
	 * リソース名とIDのマップを返します。
	 * 
	 * @return リソース名とIDのマップ
	 */
	public HashMap<String, Integer> getMap() {
		return varMap;
	}

	/**
	 * 管理していたリソースIDをすべて初期化します。
	 */
	public void reset() {
		varMap.clear();
		id = 0;
	}
}
