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
 * 「終了」コマンドを解析するローダーです。
 * 
 * @author hide92795
 */
public class CommandExit extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// boolean 終了確認
		commandLine.add(SaverStory.COMMAND_EXIT);
		int next = tokenizer.nextToken();
		if (next != StreamTokenizer.TT_WORD) {
			// 非真偽値
			throw new CommandException(tokenizer.lineno(), "終了", 1, "引数「終了確認」は真偽値でなければいけません。");
		} else {
			commandLine.add(Boolean.parseBoolean(tokenizer.sval));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "終了", -1, "引数が閉じられていません。");
		}
	}
}
