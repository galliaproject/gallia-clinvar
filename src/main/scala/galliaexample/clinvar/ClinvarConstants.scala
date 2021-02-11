package galliaexample.clinvar

import aptus.String_
import gallia._

// ===========================================================================
object ClinvarConstants {

  object SemanticSeparators {
      val Pipe  = "|"
      val Colon = ":"
      val Comma = ","
    }

    val EntrySplitter = (value: String) => value.splitBy(":", 2) // some values have more than one colon, eg "Human_Phenotype_Ontology:HP:0000608"

    val ConflictsRegex = """^(.+)\((\d+)\)$""".regex

  // ===========================================================================
  object InputFields { // TODO: could use enumeratum enum instead and loop over values in to feed vcf method
    /** dbSNP ID (i.e. rs number) */ // TODO: remove if same
    val RS = 'RS

    // ---------------------------------------------------------------------------
    /** disease name      - "ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB" - preferred disease name */
    /* D-N    */val CLNDN        = 'CLNDN

    /** disease name INCL - "For included Variant: ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB"  - preferred disease name (incl) */
    /* DIS-N  */val CLNDNINCL    = 'CLNDNINCL

    // ---------------------------------------------------------------------------
    /** disease db      - "Tag-value pairs of disease database name and identifier, e.g. OMIM:NNNNNN" - disease databases, eg: CLNDISDB=MedGen:C3808739,OMIM:615120|MedGen:CN169374 */
    /* DIS-DB */val CLNDISDB     = 'CLNDISDB

    /** disease db INCL - "For included Variant: Tag-value pairs of disease database name and identifier, e.g. OMIM:NNNNNN" - disease databases (incl) */
    /* DIS-DB */val CLNDISDBINCL = 'CLNDISDBINCL

    // ---------------------------------------------------------------------------
    val CLNHGVS    = 'CLNHGVS

    val CLNREVSTAT = 'CLNREVSTAT // "ClinVar review status for the Variation ID"
    val CLNSIG     = 'CLNSIG

    val CLNVC      = 'CLNVC
    val CLNVCSO    = 'CLNVCSO

    // ---------------------------------------------------------------------------
    /** For a haplotype or genotype that includes this variant. Reported as pairs of VariationID:clinical significance. */
    val CLNSIGINCL   = 'CLNSIGINCL // "Clinical significance for a haplotype or genotype that includes this variant. Reported as pairs of VariationID:clinical significance."

    /**
     * "the variant's clinical sources reported as tag-value pairs of database and variant identifier"
     *    eg: "Illumina_Clinical_Services_Laboratory,Illumina:455010|UniProtKB_(protein):P55084#VAR_061897"
     */
    val CLNVI        = 'CLNVI

    /** eg "Likely_pathogenic(3)%3BPathogenic(1)%3BUncertain_significance(2)" */
    val CLNSIGCONF   = 'CLNSIGCONF

    /** Gene(s) for the variant reported as gene symbol:gene id. The gene symbol and id are delimited by a colon (:) and each pair is delimited by a vertical bar (|) */
    val GENEINFO     = 'GENEINFO

    /** comma separated list of molecular consequence in the form of Sequence Ontology ID|molecular_consequence
          eg "SO:0001583|missense_variant,SO:0001623|5_prime_UTR_variant" */
    val MC           = 'MC

    val ORIGIN       = 'ORIGIN

    val SSR          = 'SSR

    // ---------------------------------------------------------------------------
    /** eg "nsv119746" (only 129 values in total); nsv accessions from dbVar for the variant (note: nsv = accession prefixes for variant regions, eg TODO) */
    val DBVARID      = 'DBVARID
  }

  // ===========================================================================
  object OutputFields extends Enumeration { // showcasing "regular" scala enum (vs enumeratum)
    type OutputFields = Value

    val
         clinvar_allele_id ,

         disease           ,
         disease_INCL      ,

         count             ,
         name              ,
         database          ,
         id                ,

         included_clinvar_variant_id, /* meaning: see 210118100341 */
         value                      , /* clinical significance */

         symbol                     ,
         entrez                     ,

         molecular_consequence,

        `Sequence Ontology ID`, /* only used temporarily */

         ESP   ,
         EXAC  ,
        `1KGP`
      = Value
  }

  // ===========================================================================
  val SsrMapping: Map[String, String] =
    Map(
        "0"    -> "unspecified",
        "1"    -> "Paralog",
        "2"    -> "byEST",
        "4"    -> "oldAlign",
        "8"    -> "Para_EST",

        "16"   -> "1kg_failed", // TODO: 1kgp?

        "17"   -> "unknown_code", // TODO: == 16 + 1 ?

        "1024" -> "other")

  // ---------------------------------------------------------------------------
  import InputFields._

  val BulkRenamings: Map[KeyW, KeyW] =
    Map(
        'ID         -> '_id,                      /* as opposed to allele and variation IDs below */
        'ALLELEID   -> 'clinvar_allele_id,        /* "the ClinVar Allele ID" */

        DBVARID     -> 'dbvar_nvs_accession,      /* DBVARID: eg "nsv119746" (only 129 values in total); nsv accessions from dbVar for the variant (note: nsv = accession prefixes for variant regions, eg TODO) */

        CLNHGVS     -> 'HGVS_expression,          /* Top-level (primary assembly, alt, or patch) HGVS expression. */

        CLNSIG      -> 'clinical_significance,    /* "Clinical significance for this single variant" */
        CLNSIGCONF  -> 'conflicting_significance, /* "Conflicting clinical significance for this single variant"*/ // TODO: nest?

        CLNVI       -> 'variant_clinical_source,  /* TODO: hard to homogenize...; "the variant's clinical sources reported as tag-value pairs of database and variant identifier"*/

        ORIGIN      -> 'allele_origin,            /* Allele origin. One or more of the following values may be added: 0 - unknown; 1 - germline; 2 - somatic; 4 - inherited; 8 - paternal; 16 - maternal; 32 - de-novo; 64 - biparental; 128 - uniparental; 256 - not-tested; 512 - tested-inconclusive; 1073741824 - other */
        SSR         -> 'variant_suspect_reason,   /* Variant Suspect Reason Codes. One or more of the following values may be added: 0 - unspecified, 1 - Paralog, 2 - byEST, 4 - oldAlign, 8 - Para_EST, 16 - 1kg_failed, 1024 - other */
      )

}

// ===========================================================================
