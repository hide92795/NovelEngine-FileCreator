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
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

/**
 * 「移動」コマンドを解析するローダーです。
 * 
 * @author hide92795
 */
public class CommandMoveChapter extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// 文字列 チャプターID
		commandLine.add(SaverStory.COMMAND_MOVE_CHAPTER);
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "移動", 1, "引数「チャプターID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.CHAPTER_ID.add(tokenizer.sval));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "移動", -1, "引数が閉じられていません。");
		}
	}
}
