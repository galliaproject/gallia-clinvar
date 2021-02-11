package galliaexample.clinvar

import aptus.{Anything_, String_}
import gallia._

// ===========================================================================
object ClinvarVcf { // 210102155202
  import ClinvarConstants._

  import InputFields._
  import OutputFields._

  // ===========================================================================
  def apply(head: HeadU): HeadU =
    head
      .unnestAllFrom('INFO)
      .remove(RS)

          // ---------------------------------------------------------------------------
          .thn(processDiseaseFields(
                CLNDN   , /* disease name, eg: "Myasthenic_syndrome,_congenital,_8|not_specified"   */
                CLNDISDB, /* disease db  , eg:        "MedGen:C3808739,OMIM:615120|MedGen:CN169374" */
              newKey = disease))

          .thn(processDiseaseFields(
                CLNDNINCL   , /* disease name INCL - eg "Small_fiber_neuropathy";        "For included Variant: ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB"*/
                CLNDISDBINCL, /* disease db   INCL - eg "MedGen:C0220754", "OMIM:253260; "For included Variant: Tag-value pairs of disease database name and identifier, e.g. OMIM:NNNNNN" */
              newKey = disease_INCL))

          // ---------------------------------------------------------------------------
          .untuplify1b(CLNSIGINCL ~> 'clinical_significance_for_including)
            .withSplitters(SemanticSeparators.Pipe, SemanticSeparators.Colon)
              .asNewKeys(
                  included_clinvar_variant_id  /* meaning: see 210118100341 */,
                  value                        /* clinical significance */)

          // ---------------------------------------------------------------------------
          .untuplify1b(GENEINFO ~> 'genes)
            .withSplitters(SemanticSeparators.Pipe, SemanticSeparators.Colon)
              .asNewKeys(symbol, entrez)

          // ---------------------------------------------------------------------------
          .untuplify1b(MC ~> 'molecular_consequences)
            // inconsistently using pipe here as tuple separator, eg "SO:0001583|missense_variant,SO:0001623|5_prime_UTR_variant" */
            .withSplitters(SemanticSeparators.Comma, SemanticSeparators.Pipe)
              .asNewKeys(
                  /* Sequence Ontology ID  */ 'term, // eg "SO:0001583"
                  /* molecular_consequence */ 'name) // eg "missense_variant"

          // ---------------------------------------------------------------------------
          .untuplify1b(CLNVI)
            .withSplitters(SemanticSeparators.Pipe, EntrySplitter)
              .asNewKeys(name, id /* mostly internal IDs */)

          // ---------------------------------------------------------------------------
          .untuplify1b(CLNSIGCONF) // eg "Likely_pathogenic(3)%3BPathogenic(1)%3BUncertain_significance(2)"
            .withSplitters(
                arraySplitter = "%3B", // see br210112171706 for comma; TODO: figure out the (\d) part, only a 5 distinct values if not for these
                  entriesSplitter = _.extractGroups(ConflictsRegex).get)
              .asNewKeys(value, count)

          // ---------------------------------------------------------------------------
          .convert  (SSR).toNonRequired
          .translate(SSR) // "Variant Suspect Reason Codes. One or more of the following values may be added">
            .usingStrict(SsrMapping) // TODO: how come never actually summed, unlike ORIGIN? really only 1, 16 and 17 in just a handful of values...

          // ---------------------------------------------------------------------------
          .split(ORIGIN).by(ClinvarOriginDecoding.apply(_))

          // ---------------------------------------------------------------------------
          .nest(
                CLNVC   ~> 'name, // eg "single_nucleotide_variant"; "Variant type"
                CLNVCSO ~> 'term) // eg "SO:0001483"; "Sequence Ontology id for variant type"
              .under('variant_type)

          // ---------------------------------------------------------------------------
          .rename(CLNREVSTAT ~> 'variation_review_status) // not much else to do here

          // ===========================================================================
          // AF

          .nest(
                'AF_ESP  ~>  ESP  , // allele frequencies from GO-ESP
                'AF_EXAC ~>  EXAC , // allele frequencies from ExAC
                'AF_TGP  ~> `1KGP`) // allele frequencies from KGP
              .under        ('AF)
            .convert        ('AF).toNonRequired           // won't be required after t210122162650 is addressed
            .transformObject('AF).using {
              _ .convert(ESP, EXAC, `1KGP`).toNonRequired // won't be required after t210122162650 is addressed
                .forLeafPaths(_ convert _ toDouble) }

          // ---------------------------------------------------------------------------
          // other renamings

          .rename(BulkRenamings) // TODO: must split GENEINFO

  // ===========================================================================
  def processDiseaseFields(nameKey: Key, dbKey: Key, newKey: KeyW)(headO: HeadO): HeadU =
    headO

      .zipStrings(
            nameKey ~> 'preferred_name, // eg: "Myasthenic_syndrome,_congenital,_8|not_specified"
            dbKey   ~> 'terms         ) // eg:        "MedGen:C3808739,OMIM:615120|MedGen:CN169374",
          .splitBy(SemanticSeparators.Pipe)
            .underNewKey(newKey)

      .convert(newKey.value).toNonRequired // eg disease_INCL...

      // ---------------------------------------------------------------------------
      .transform(_.objz(newKey.value)).using {
          // eg "MedGen:C3808739,OMIM:615120" - "Tag-value pairs of disease database name and identifier, e.g. OMIM:NNNNNN"
          _ .removeIfValueFor('terms).is(".")
            .untuplify1b('terms)
              .withSplitters(SemanticSeparators.Comma, EntrySplitter)
                .asNewKeys(database, id) }

}

// ===========================================================================
