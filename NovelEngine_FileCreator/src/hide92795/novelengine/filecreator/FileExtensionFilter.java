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

import java.io.File;
import java.io.FilenameFilter;

/**
 * ファイルを拡張子でフィルターするためのフィルタークラスです。
 * 
 * @author hide92795
 */
public class FileExtensionFilter implements FilenameFilter {
	/**
	 * 対象とする拡張子を表します。
	 */
	private final String extension;

	/**
	 * 特定の拡張子のファイルだけを選別するフィルターを生成します。
	 * 
	 * @param extension
	 *            拡張子
	 */
	public FileExtensionFilter(String extension) {
		this.extension = extension;
	}

	@Override
	public boolean accept(File dir, String name) {
		File file = new File(name);
		if (file.isDirectory()) {
			return false;
		}
		return (name.endsWith(extension));
	}

}
