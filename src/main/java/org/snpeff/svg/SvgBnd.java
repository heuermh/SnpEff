package org.snpeff.svg;

import org.snpeff.interval.Transcript;
import org.snpeff.interval.VariantBnd;

/**
 * Create an SVG representation of a BND (translocation) variant
 *
 *
 * In a VCF file, there are four possible translocations (BND) entries:
 *
 * 				REF ALT Meaning
 * 	type 1:		s t[p[ piece extending to the right of p is joined after t
 * 	type 2:		s t]p] reverse comp piece extending left of p is joined after t
 * 	type 3:		s ]p]t piece extending to the left of p is joined before t
 * 	type 4:		s [p[t reverse comp piece extending right of p is joined before t
 *
 */
public class SvgBnd extends Svg {

	public static final int BND_CURL_SIZE = 50;

	VariantBnd varBnd;
	Svg svgTr1, svgTr2;

	/**
	 * Create transcript and variant Svgs
	 */
	public static String plotTranslocation(Transcript tr1, Transcript tr2, VariantBnd varBnd) {
		Svg svgScale1 = new SvgScale(tr1, null);
		Svg svgTr1 = Svg.factory(tr1, svgScale1);
		Svg svgSpacer = new SvgSpacer(tr1, svgTr1);
		Svg svgScale2 = new SvgScale(tr2, svgSpacer);
		svgScale2.setScaleX();
		Svg svgTr2 = Svg.factory(tr2, svgScale2);
		Svg svgBnd = new SvgBnd(varBnd, svgTr1, svgTr2);

		String svgStr = svgTr1.open() //
				+ svgTr1 + svgScale1 //
				+ svgTr2 + svgScale2 //
				+ svgBnd //
				+ svgTr1.close();

		return svgStr;
	}

	public SvgBnd(VariantBnd varBnd, Svg svgTr1, Svg svgTr2) {
		super();
		this.varBnd = varBnd;
		this.svgTr1 = svgTr1;
		this.svgTr2 = svgTr2;
		lineStrokeWidth = 10;
		lineColor = "#ff00ff";
	}

	/**
	 * Create a 'bnd type 1'
	 */
	String bndType1() {
		StringBuilder sb = new StringBuilder();
		int pos1 = varBnd.getStart();
		double x1 = svgTr1.pos2coord(pos1);
		double y1 = svgTr1.baseY + svgTr1.rectHeight / 2;

		int pos2 = varBnd.getEndPoint().getStart();
		double x2 = svgTr2.pos2coord(pos2);
		double y2 = svgTr2.baseY + svgTr2.rectHeight / 2;

		double x3 = (x1 + x2) / 2;

		sb.append(line(x1, y1, x3, y1));
		sb.append(line(x3, y1 - lineStrokeWidth / 2, x3, y2 + lineStrokeWidth / 2));
		sb.append(line(x2, y2, x3, y2));
		return sb.toString();
	}

	/**
	 * Create a 'bnd type 2'
	 */
	String bndType2() {
		StringBuilder sb = new StringBuilder();
		int pos1 = varBnd.getStart();
		double x1 = svgTr1.pos2coord(pos1);
		double y1 = svgTr1.baseY + svgTr1.rectHeight / 2;

		int pos2 = varBnd.getEndPoint().getStart();
		double x2 = svgTr2.pos2coord(pos2);
		double y2 = svgTr2.baseY + svgTr2.rectHeight / 2;

		double x3 = Math.min(sizeX, Math.max(x1, x2) + BND_CURL_SIZE);

		sb.append(line(x1, y1, x3, y1));
		sb.append(line(x3, y1 - lineStrokeWidth / 2, x3, y2 + lineStrokeWidth / 2));
		sb.append(line(x2, y2, x3, y2));
		return sb.toString();
	}

	/**
	 * Create a 'bnd type 3'
	 */
	String bndType3() {
		return bndType1(); // The representation looks the same as type 1
	}

	/**
	 * Create a 'bnd type 4'
	 */
	String bndType4() {
		StringBuilder sb = new StringBuilder();
		int pos1 = varBnd.getStart();
		double x1 = svgTr1.pos2coord(pos1);
		double y1 = svgTr1.baseY + svgTr1.rectHeight / 2;

		int pos2 = varBnd.getEndPoint().getStart();
		double x2 = svgTr2.pos2coord(pos2);
		double y2 = svgTr2.baseY + svgTr2.rectHeight / 2;

		double x3 = Math.max(0, Math.min(x1, x2) - BND_CURL_SIZE);

		sb.append(line(x1, y1, x3, y1));
		sb.append(line(x3, y1 - lineStrokeWidth / 2, x3, y2 + lineStrokeWidth / 2));
		sb.append(line(x2, y2, x3, y2));
		return sb.toString();
	}

	@Override
	public String toString() {
		if (!varBnd.isBefore()) {
			if (!varBnd.isLeft()) return bndType1();
			return bndType2();
		}

		if (varBnd.isLeft()) return bndType3();
		return bndType4();
	}

}
