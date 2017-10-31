package style;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class RoundedLabel extends Canvas {

	private String text   = "";
	private static final int MARGIN = 3;

	public RoundedLabel(Composite parent, int style) {
		
		super(parent, style);

		addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				RoundedLabel.this.paintControl(e);
			}
		});
	}

	void paintControl(PaintEvent e) {
		
		Point rect = getSize();
		e.gc.fillRectangle(0, 0, rect.x, rect.x);
		e.gc.drawRoundRectangle(MARGIN, MARGIN, rect.x - 2 * MARGIN - 1, rect.y - 2 * MARGIN - 1, MARGIN, MARGIN);
		e.gc.drawText(text, 2 * MARGIN, 2 * MARGIN);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		redraw();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		
		GC gc = new GC(this);

		Point pt = gc.stringExtent(text);

		gc.dispose();

		return new Point(pt.x + 4 * MARGIN, pt.y + 4 * MARGIN);
	}

}
