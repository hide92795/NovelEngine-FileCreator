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

import hide92795.novelengine.filecreator.saver.SaverFigure;
import java.awt.Color;
import java.util.Map;
import java.util.Set;
import org.msgpack.packer.Packer;

/**
 * 円形のフィギュアデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class FigureSaverCircle implements FigureSaver {
	@Override
	public void save(Map<?, ?> map_f, Packer packer) throws Exception {
		packer.write(SaverFigure.FIGURE_CIRCLE);
		Map<?, ?> center = (Map<?, ?>) map_f.get("Center");
		int cx = (Integer) center.get("x");
		int cy = (Integer) center.get("y");

		int radius = (Integer) map_f.get("Radius");
		int division = (Integer) map_f.get("Division");

		packer.write(cx);
		packer.write(cy);
		packer.write(division);

		for (int i = division; i > 0; i--) {
			float rate = (float) i / division;
			float x = (float) (radius * Math.cos(2.0 * Math.PI * rate));
			float y = (float) (radius * Math.sin(2.0 * Math.PI * rate));
			x += cx;
			y += cy;
			packer.write(x);
			packer.write(y);
		}

		Map<?, ?> lines = (Map<?, ?>) map_f.get("Lines");
		if (lines != null) {
			Set<?> set = lines.keySet();
			int num = set.size();
			packer.write(num);
			for (Object line_o : set) {
				String line = line_o.toString();
				String[] lineEnds = line.split("-");
				int start = Integer.valueOf(lineEnds[0]);
				int end = Integer.valueOf(lineEnds[1]);

				Map<?, ?> lineInfo = (Map<?, ?>) lines.get(line_o);
				String color_s = lineInfo.get("Color").toString();
				int color = Color.decode("#" + color_s).getRGB();
				byte alpha = ((Integer) lineInfo.get("Alpha")).byteValue();
				int width = (Integer) lineInfo.get("Width");

				packer.write(start - 1);
				packer.write(end - 1);
				packer.write(color);
				packer.write(alpha);
				packer.write(width);
			}
		}
	}
}
