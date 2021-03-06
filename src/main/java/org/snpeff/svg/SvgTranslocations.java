package org.snpeff.svg;

import java.util.List;

import org.snpeff.SnpEff;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Gene;
import org.snpeff.interval.Transcript;
import org.snpeff.interval.Variant;
import org.snpeff.interval.Variant.VariantType;
import org.snpeff.interval.VariantBnd;
import org.snpeff.snpEffect.SnpEffectPredictor;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEffect;
import org.snpeff.vcf.VcfEntry;

/**
 * Create an SVG representation of a Marker
 */
public class SvgTranslocations extends SnpEff {

	boolean save;
	String vcfFileName;

	public static void main(String[] args) {
		String genomeVer = "testHg19Chr2";
		String vcfFileName = "zz.vcf";
		SvgTranslocations svg = new SvgTranslocations(genomeVer, vcfFileName);
		svg.setVerbose(true);
		svg.setDebug(true);
		svg.setSave(true);
		svg.loadConfig();
		svg.loadDb();
		svg.plots();
	}

	public SvgTranslocations(String genomeVer, String vcfFileName) {
		this.genomeVer = genomeVer;
		this.vcfFileName = vcfFileName;
	}

	void plot(VariantBnd var, String gene1, String gene2) {
		if (debug) System.out.println("\tGenes: " + gene1 + "\t" + gene2);
		SnpEffectPredictor sep = config.getSnpEffectPredictor();

		Gene g1 = sep.getGene(gene1);
		if (g1 == null) {
			Timer.showStdErr("Gene '" + gene1 + "' not found. Skipping plot");
			return;
		}

		Gene g2 = sep.getGene(gene2);
		if (g2 == null) {
			Timer.showStdErr("Gene '" + gene2 + "' not found. Skipping plot");
			return;
		}

		for (Transcript tr1 : g1)
			for (Transcript tr2 : g2)
				plot(var, tr1, tr2);
	}

	void plot(VariantBnd var, Transcript tr1, Transcript tr2) {
		if (debug) System.out.println("\tTranscripts: " + tr1.getId() + "\t" + tr2.getId());
		String svgStr = SvgBnd.plotTranslocation(tr1, tr2, var);

		// Save to file
		if (save) {
			String fileName = Gpr.HOME + "/z_" + tr1.getId() + "-" + tr2.getId() + ".html";
			Gpr.toFile(fileName, svgStr);
			Gpr.debug("Saved to file " + fileName);
		}
	}

	void plots() {
		// Read VCF file (one line)
		String vcfFile = "zz.vcf";
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		for (VcfEntry ve : vcf) {
			if (debug) System.out.println(ve);

			List<Variant> vars = ve.variants();
			if (vars.isEmpty()) continue;

			Variant var = vars.get(0);
			if (var.getVariantType() != VariantType.BND) continue;

			for (VcfEffect veff : ve.getVcfEffects()) {
				if (debug) System.out.println("\t" + veff);
				String geneStr = veff.getGeneId();
				String genes[] = geneStr.split("&");

				String trStr = veff.getTranscriptId();
				System.out.println("\tTranscript:" + trStr);
				String geneName1 = genes[0];
				String geneName2 = genes[1];
				plot((VariantBnd) var, geneName1, geneName2);
			}
		}
	}

	public void setSave(boolean save) {
		this.save = save;
	}

}
