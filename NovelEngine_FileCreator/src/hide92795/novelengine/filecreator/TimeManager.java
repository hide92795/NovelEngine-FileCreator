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
 * 処理の経過時間を管理します。
 * 
 * @author hide92795
 */
public class TimeManager {
	/**
	 * カウント開始時の時刻を表します。
	 */
	private static long before;

	/**
	 * 処理の開始をマークします。
	 */
	public static void start() {
		before = System.currentTimeMillis();
	}

	/**
	 * 処理を完了し、経過時間を出力します。
	 * 
	 * @param bef
	 *            時間の前に付け足す文字列
	 * @param aft
	 *            時間の後に付け足す文字列
	 */
	public static void end(String bef, String aft) {
		long after = System.currentTimeMillis();
		System.out.println(bef + (double) (after - before) / 1000 + aft);
	}
}
