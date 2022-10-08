package galliaexample.vcf

import aptus.{String_, Seq_}
import gallia._

// ===========================================================================
object Vcf { // 210211093714

  /**  VCF specification: https://samtools.github.io/hts-specs/VCFv4.2.pdf */
  def processLines(infoKey1: KeyW, more: KeyW*): HeadS => HeadS = // could be generalized, many VCF will follow a similar pattern
    _ .filterBy(_line).matches(!_.startsWith("#"))

      .fission(_.string(_line))
          .as("CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER", "INFO")
            .using {
              _ .splitXsv('\t' /* accounts for escaping */)
                .force.tuple8 }

      .convert("POS").toInt // ID is also an integer but not one meant to be used as such
      .removeIfValueFor("QUAL", "FILTER").is(".")

      // ---------------------------------------------------------------------------
      // "deserialize" -> see https://github.com/galliaproject/gallia-core#why-does-the-terminology-sometimes-sound-funny-or-full-on-neological
      .deserialize2a("INFO")
        .withSplitters( // see VCF specification (eg "RS=1235;ALLELEID=...")
            entriesSplitter = ";",
              entrySplitter = "=")
          .asNewKeys(infoKey1, more:_*)

}

// ===========================================================================
