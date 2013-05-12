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
package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

/**
 * ストーリーデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public abstract class Command {
	/**
	 * コマンドを解析してコマンドラインにデータを追加します。
	 * 
	 * @param tokenizer
	 *            ソースデータを解析する {@link java.io.StreamTokenizer StreamTokenizer}
	 * @param commandLine
	 *            データを保存するコマンドライン
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	public abstract void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception;

	/**
	 * 次の引数を読み込み、その種類を返します。
	 * 
	 * @param tokenizer
	 *            ソースデータを解析する {@link java.io.StreamTokenizer StreamTokenizer}
	 * @return 次の引数
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	protected int nextArgument(StreamTokenizer tokenizer) throws Exception {
		int token = tokenizer.nextToken();
		if (token != SaverStory.COMMA) {
			throw new CommandException(tokenizer.lineno(), "区切り文字", -1, "引数が不足している、もしくは区切り文字が不正です。");
		}
		token = tokenizer.nextToken();
		return token;
	}
}
