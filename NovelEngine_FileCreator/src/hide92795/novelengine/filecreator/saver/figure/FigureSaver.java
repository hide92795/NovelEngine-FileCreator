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
package hide92795.novelengine.filecreator.saver.figure;

import java.util.Map;
import org.msgpack.packer.Packer;

/**
 * フィギュアデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public interface FigureSaver {
	/**
	 * 読み込んだファイルからフィギュアデータを作成します。
	 * 
	 * @param map_f
	 *            読み込んだファイルのデータ
	 * @param packer
	 *            書き込み先のPacker
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	void save(Map<?, ?> map_f, Packer packer) throws Exception;
}
