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

/**
 * コマンドの解析中にエラーが起きた際に呼ばれる例外です。
 * 
 * @author hide92795
 */
public class CommandException extends Exception {
	/**
	 * このクラスはシリアライズされるべきではありません。
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * コマンドの解析エラーを生成します。
	 * 
	 * @param line
	 *            解析中の行番号
	 * @param commandName
	 *            解析中のコマンド名
	 * @param argNumber
	 *            解析中のコマンドの引数番号
	 * @param desc
	 *            詳細な情報
	 */
	public CommandException(int line, String commandName, int argNumber, String desc) {
		super(new StringBuilder().append("コマンド解析中にエラーが発生しました。\n行番号：").append(line).append("\nコマンド名：")
				.append(commandName).append("\n引数：").append(argNumber).append("\n").append(desc).toString());
	}
}
