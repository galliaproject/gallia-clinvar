This is only intended to showcase processing in Gallia, it is in now complete nor thoroughly tested at the moment. Use output at your own risk.

See gallia-core [README](https://github.com/galliaproject/gallia-core/blob/init/README.md).

<a name="transformations"></a>Uses [_Gallia_ transformations](https://github.com/galliaproject/gallia-clinvar/blob/init/src/main/scala/galliaexample/clinvar/ClinvarVcf.scala#L14)

<a name="input"></a>to turn rows such as:

```plain
#CHROM  POS      ID      REF  ALT  QUAL  FILTER  INFO
1       1049066  706774  G    A    .     .       AF_EXAC=0.00007;AF_TGP=0.00040;ALLELEID=694996;CLNDISDB=MONDO:MONDO:0014052,MedGen:C3808739,OMIM:615120;CLNDN=Myasthenic_syndrome,_congenital,_8;CLNHGVS=NC_000001.11:g.1049066G>A;CLNREVSTAT=criteria_provided,_single_submitter;CLNSIG=Benign;CLNVC=single_nucleotide_variant;CLNVCSO=SO:0001483;GENEINFO=AGRN:375790;MC=SO:0001627|intron_variant;ORIGIN=1;RS=201995572
```

<a name="output"></a>into objects like:

```json
{
  "CHROM": "1",
  "POS": 1049066,
  "_id": "706774",
  "REF": "G",
  "ALT": "A",
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
```

