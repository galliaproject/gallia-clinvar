package galliaexample
package clinvar

import aptus.Anything_ // for .thn()
import gallia._

// ===========================================================================
object ClinvarVcfDriver {

  /**
   *  retrieved via
   *    {{{ curl https://ftp.ncbi.nlm.nih.gov/pub/clinvar/vcf_GRCh38/clinvar_20210208.vcf.gz | gzip -d | gzip -c > /data/clinvar/clinvar_recompressed.vcf.gz }}}
   *
   *  for some reason it needs to be re-gzipped, else processing trips on it:
   *  - either something is wrong with https://github.com/galliaproject/gallia-core/blob/init/src/main/scala/aptus/utils/InputStreamUtils.scala#L59-L62,
   *  - or something is off with the source data's compression
   */
  val InputFile = "/data/clinvar/clinvar_recompressed.vcf.gz"

  // ===========================================================================
  /**
    e.g. turns VCF row:
      {{{
        #CHROM  POS      ID      REF  ALT  QUAL  FILTER  INFO
        1       1049066  706774  G    A    .     .       AF_EXAC=0.00007;AF_TGP=0.00040;ALLELEID=694996;CLNDISDB=MONDO:MONDO:0014052,MedGen:C3808739,OMIM:615120;CLNDN=Myasthenic_syndrome,_congenital,_8;CLNHGVS=NC_000001.11:g.1049066G>A;CLNREVSTAT=criteria_provided,_single_submitter;CLNSIG=Benign;CLNVC=single_nucleotide_variant;CLNVCSO=SO:0001483;GENEINFO=AGRN:375790;MC=SO:0001627|intron_variant;ORIGIN=1;RS=201995572
      }}}

    into:
      {{{
        {
          "chromosome": "1",
          "position": 1049066,
          "_id": "706774",
          "ref": "G",
          "alt": "A",
          "clinvar_allele_id": "694996",
          "HGVS_expression": "NC_000001.11:g.1049066G>A",
          "variation_review_status": "criteria_provided,_single_submitter",
          "clinical_significance": "Benign",
          "allele_origin": [ "germline" ],
          "disease": [
            { "preferred_name": "Myasthenic_syndrome,_congenital,_8",
              "terms": [
                { "database": "MONDO",
                  "id": "MONDO:0014052" },
                { "database": "MedGen",
                  "id": "C3808739" },
                { "database": "OMIM",
                  "id": "615120" } ] } ],
          "genes": [
            { "symbol": "AGRN",
              "entrez": "375790" } ],
          "molecular_consequences": [
            { "term": "SO:0001627",
              "name": "intron_variant" } ],
          "variant_type": {
            "name": "single_nucleotide_variant",
            "term": "SO:0001483" },
          "AF": {
            "EXAC": 0.00007,
            "1KGP": 0.00040 }
        }
      }}}
   */
  val OutputFile = "/tmp/clinvar.jsonl.gz"

  // ===========================================================================
  def main(args: Array[String]) {
    InputFile

        .stream(_.lines.iteratorMode)
        .logProgress(1000)
        .thn(vcf.Vcf.processLines(
              // INFO keys; could also extract them from VCF header if it is well-formed (not always the case...)
              'ALLELEID, 'RS,
              'CLNDN, 'CLNDNINCL, 'CLNDISDB, 'CLNDISDBINCL, 'CLNHGVS, 'CLNREVSTAT,
              'CLNSIG, 'CLNVC, 'CLNVCSO, 'CLNSIGINCL, 'CLNVI, 'CLNSIGCONF,
              'GENEINFO, 'DBVARID, 'ORIGIN, 'MC, 'SSR,
              'AF_ESP, 'AF_EXAC, 'AF_TGP))
        .map(ClinvarVcf.apply _)

      .write(OutputFile)

    ()
  }


}

// ===========================================================================
