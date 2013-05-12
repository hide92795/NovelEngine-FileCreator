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
 * 「もし」コマンドを解析するローダーです。
 * 
 * @author hide92795
 */
public class CommandIF extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// 文字列 条件, 数値 真, 数値 偽
		commandLine.add(SaverStory.COMMAND_IF);
		int next = tokenizer.nextToken();
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "もし", 1, "引数「条件文」は文字列でなければいけません。");
		} else {
			String condition = SaverStory.searchCondition(tokenizer.sval);
			String[] args = tokenizer.sval.split(condition);
			if (args.length != 2) {
				// 条件不足
				throw new CommandException(tokenizer.lineno(), "もし", 1, "条件文が不正です。");
			}
			commandLine.add(SaverStory.parseCondition(condition));
			SaverStory.parseVariable(args[0], commandLine);
			SaverStory.parseVariable(args[1], commandLine);
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "もし", 2, "引数「移動先シーンID（真）」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "もし", 3, "引数「移動先シーンID（偽）」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.SCENE_ID.add(tokenizer.sval));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "もし", -1, "引数が閉じられていません。");
		}
	}
}
