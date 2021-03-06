---
layout: default
title: "refseq_masher: What's in your sequence data?"
search_title: "refseq_masher: What's in your sequence data?"
description: "Using refseq_masher in IRIDA."
---


refseq_masher: What's in your sequence data?
============================================
{:.no_toc}

[refseq_masher] searches your sequence data against a [Mash] database of 54,925 [NCBI RefSeq Genomes] to find what NCBI RefSeq genomes match or are contained within your sequence data.

This tutorial will run [refseq_masher] against a publicly available reads set.  

* TOC
{:toc}


Get tutorial reads data 
------------------------------------------

Let's get the WGS data for `SRR1203042` from [EBI]:

> Illumina MiSeq paired end sequencing; Whole genome shotgun sequencing of Salmonella enterica subsp. enterica serovar Abony str. FNW19H84 by Illumina MiSeq


Download forward and reverse reads for `SRR1203042`:

- [SRR1203042_1.fastq.gz]
- [SRR1203042_2.fastq.gz] 


Initial Data
============

It is assumed the sequence files (forward and reverse) `SRR1203042_1.fastq.gz` and `SRR1203042_2.fastq.gz` have been uploaded into a sample as described in the [Web Upload Tutorial][]. Before starting this tutorial you should have a project with a sample as seen below:

![refseq-masher-samples]


Add Sample to Cart
------------------

Select sample "SRR1203042" and add it to your Cart:

![add-to-cart]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button]

Select refseq_masher Pipeline
-----------------------------

Once inside the cart all available pipelines will be listed in the main area of the page.

![pipeline-select]

Configure and Launch refseq_masher Analysis
-------------------------------------------

![refseq-pipeline-page]

Modify the parameters to run `refseq_masher` if desired:

![refseq-pipeline-customize]

Click the "Launch" button to submit the analysis. 

![launch-button]


Monitoring Analysis Status
--------------------------

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu or click the **Let's see how this pipeline is doing** button.

![view-your-analyses][]

The will bring you to a page where you can monitor the status of each launched workflow.

![monitor-analyses][]

Clicking the pipeline name will bring you to a page for that analysis pipeline. It will take a while for the RefSeqMasher analysis pipeline to complete. Along the top of the page you can check the current step of the analysis and at the bottom of the **Details** tab you can select if you would like to receive an email upon pipeline completion or error. The email option is only available if the analysis is not in `COMPLETED` or `ERROR` state.

![analysis-in-progress]

When the `Ref Seq Masher`analysis has completed, you will be able to view the results of the analysis. Note that not all files have an available preview and as such are not displayed in the Output File Preview but are downloaded when selecting the `Download All Files` button.

![refseq-results]

You should see 2 files under "Output File Preview":

- `SRR1203042-refseq-masher-matches.tsv` 
    - Top matching NCBI Genomes to sample `SRR1203042` 
- `SRR1203042-refseq-masher-contains.tsv`
    - NCBI Genomes contained in sample `SRR1203042`

To download individual files select the **...** next to the Download All Files and select the file to download.

![refseq-download-individual-files][]

To download al the files generated by the analysis, please select the **Download All Files** button.

![refseq-download-all-files][]

Viewing and Interpreting the Results
------------------------------------

### Matches - `SRR1203042-refseq-masher-matches.tsv`

Below is the top result from `SRR1203042-refseq-masher-matches.tsv` transposed for readability:

| Field | Value |
| ==== + === |
| sample | SRR1203042 |
| top_taxonomy_name | Salmonella enterica subsp. enterica serovar Abony str. 0014 |
| distance | 0.00650877 |
| pvalue | 0 |
| matching | 328/400 |
| full_taxonomy | Bacteria; Proteobacteria; Gammaproteobacteria; Enterobacterales; Enterobacteriaceae; Salmonella; enterica; subsp. enterica; serovar Abony; str. 0014 |
| taxonomic_subspecies | Salmonella enterica subsp. enterica |
| taxonomic_species | Salmonella enterica |
| taxonomic_genus | Salmonella |
| taxonomic_family | Enterobacteriaceae |
| taxonomic_order | Enterobacterales |
| taxonomic_class | Gammaproteobacteria |
| taxonomic_phylum | Proteobacteria |
| taxonomic_superkingdom | Bacteria |
| subspecies | enterica |
| serovar | Abony |
| plasmid |  |
| bioproject | PRJNA224116 |
| biosample | SAMN01823751 |
| taxid | 1029983 |
| assembly_accession | GCF_000487615.2 |
| match_id | ./rcn/refseq-NZ-1029983-PRJNA224116-SAMN01823751-GCF_000487615.2-.-Salmonella_enterica_subsp._enterica_serovar_Abony_str._0014.fna |

Given that our sample, `SRR1203042`, was from an Illumina MiSeq sequencing run of strain `Salmonella enterica subsp. enterica serovar Abony str. FNW19H84`, the top `refseq_masher` result confirms that the WGS is *Salmonella enterica* subsp. enterica serovar Abony. 

*For more info on interpreting refseq_masher **matches** results, see [refseq_masher matches documentation][matches]*

### Contains - `SRR1203042-refseq-masher-contains.tsv`

> If a read set potentially has multiple genomes, it can be “screened” against the database to estimate how well each genome is contained in the read set.

- [Mash Screen] tutorial


Below are the first 5 rows and columns of `SRR1203042-refseq-masher-contains.tsv`:

| sample | top_taxonomy_name | identity | shared_hashes | median_multiplicity | 
| ====== + ================= + ======== + ============= + =================== |
| SRR1203042 | Salmonella enterica subsp. enterica serovar Typhimurium | 0.99953 | 397/400 | 36 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Typhimurium | 0.997614 | 385/400 | 285 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Typhimurium | 0.997614 | 385/400 | 285 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Choleraesuis | 0.997614 | 385/400 | 284 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Choleraesuis | 0.997614 | 385/400 | 284 | 

We can see that `SRR1203042` mostly contains k-mers belonging to *Salmonella enterica* subsp. enterica NCBI RefSeq Genomes. 

About some of the fields in `SRR1203042-refseq-masher-contains.tsv`: 

- `sample` - Your sample name 
- `identity` - Proportion of identical hashes or k-mers between your sample and an NCBI RefSeq Genome in the Mash Sketch database
- `shared_hashes` - Number of hashes shared between your sample and an NCBI RefSeq Genome in the Mash Sketch database
- `median_multiplicity` - "median multiplicity is computed for shared hashes, based on the number of observations of those hashes within the pool" (from `mash screen -h` with Mash v2.0)

*For more info on interpreting refseq_masher **contains** results, see [refseq_masher contains documentation][contains]* and the [Mash Screen] documentation.


Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![refseq-provenance]

The provenance is displayed on a per file basis. Clicking on `refseq-masher-matches.tsv` file will display it's provenance. Expanding each tool will display the parameters that the tool was executed with.

![refseq-provenance-tools]


Viewing Pipeline Details
========================

To view analysis details, please select the **Settings** tab. From here you can view the analysis name, analysis description, analysis id, pipeline and pipeline version used by the analysis, analysis priority, when the analysis was created, and duration of the analysis.

![refseq-settings]

To edit an analysis name, please select the Pencil icon next to the analysis name. Once you have edited the analysis name, pressing the `ENTER` key on your keyboard or clicking anywhere outside of the text box will update the name. To cancel editing a name you can either hit the `ESC` key on your keyboard or if the name has not been changed you can also click anywhere outside of the text box.

![refseq-settings-edit-name]

To view samples used by the analysis, please select the **Samples** tab.

![refseq-settings-samples]

To share analysis results with other projects, please select the **Manage Results** tab.

![refseq-settings-share]

To delete an analysis, please select the **Delete Analysis** tab.

![refseq-settings-delete]




[add-to-cart]: images/add-to-cart.png
[analysis-in-progress]: images/analysis-in-progress.png
[cart-button]: images/cart-button.png
[contains]: https://github.com/phac-nml/refseq_masher#contains---find-what-ncbi-refseq-genomes-are-contained-in-your-input-sequences
[EBI]: https://www.ebi.ac.uk/ena/data/view/SRR1203042&display=html
[launch-button]: ../../../images/tutorials/common/pipelines/ready-to-launch-button.png
[Mash]: https://genomebiology.biomedcentral.com/articles/10.1186/s13059-016-0997-x
[Mash Screen]: https://mash.readthedocs.io/en/latest/tutorials.html#screening-a-read-set-for-containment-of-refseq-genomes
[matches]: https://github.com/phac-nml/refseq_masher#matches---find-the-closest-matching-ncbi-refseq-genomes-in-your-input-sequences
[monitor-analyses]: images/monitor-analyses.png
[NCBI RefSeq Genomes]: https://www.ncbi.nlm.nih.gov/genome
[pipeline-select]: images/pipeline-select.png
[refseq-download-all-files]: images/refseq-download-all-files.png
[refseq-download-individual-files]: images/refseq-download-individual-files.png
[refseq-pipeline-customize]: images/refseq-pipeline-customize.png
[refseq_masher]: https://github.com/phac-nml/refseq_masher
[refseq-masher-samples]: images/refseq-masher-samples.png
[refseq-pipeline-page]: images/refseq-pipeline-page.png
[refseq-provenance]: images/refseq-provenance.png
[refseq-provenance-tools]: images/refseq-provenance-tools.png
[refseq-results]: images/refseq-results.png
[refseq-settings]: images/refseq-settings.png
[refseq-settings-delete]: images/refseq-settings-delete.png
[refseq-settings-edit-name]: images/refseq-settings-edit-name.png
[refseq-settings-samples]: images/refseq-settings-samples.png
[refseq-settings-share]: images/refseq-settings-share.png
[seq-uploaded]: images/seq-uploaded.png
[SRR1203042_1.fastq.gz]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_1.fastq.gz
[SRR1203042_2.fastq.gz]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_2.fastq.gz
[upload-fastqs]: images/upload-fastqs.png
[view-your-analyses]: images/view-your-analyses.png
[Web Upload Tutorial]: ../web-upload/