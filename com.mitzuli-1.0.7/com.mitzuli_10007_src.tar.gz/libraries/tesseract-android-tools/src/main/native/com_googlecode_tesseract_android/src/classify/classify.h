///////////////////////////////////////////////////////////////////////
// File:        classify.h
// Description: classify class.
// Author:      Samuel Charron
//
// (C) Copyright 2006, Google Inc.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
///////////////////////////////////////////////////////////////////////

#ifndef TESSERACT_CLASSIFY_CLASSIFY_H__
#define TESSERACT_CLASSIFY_CLASSIFY_H__

#include "adaptive.h"
#include "ccstruct.h"
#include "classify.h"
#include "dict.h"
#include "featdefs.h"
#include "fontinfo.h"
#include "intfx.h"
#include "intmatcher.h"
#include "normalis.h"
#include "ratngs.h"
#include "ocrfeatures.h"
#include "unicity_table.h"

class ScrollView;
class WERD_CHOICE;
class WERD_RES;
struct ADAPT_RESULTS;
struct NORM_PROTOS;

static const int kUnknownFontinfoId = -1;
static const int kBlankFontinfoId = -2;

namespace tesseract {

struct ShapeRating;
class ShapeTable;

// How segmented is a blob. In this enum, character refers to a classifiable
// unit, but that is too long and character is usually easier to understand.
enum CharSegmentationType {
  CST_FRAGMENT,  // A partial character.
  CST_WHOLE,     // A correctly segmented character.
  CST_IMPROPER,  // More than one but less than 2 characters.
  CST_NGRAM      // Multiple characters.
};

class Classify : public CCStruct {
 public:
  Classify();
  virtual ~Classify();
  Dict& getDict() {
    return dict_;
  }

  const ShapeTable* shape_table() const {
    return shape_table_;
  }

  /* adaptive.cpp ************************************************************/
  ADAPT_TEMPLATES NewAdaptedTemplates(bool InitFromUnicharset);
  int GetFontinfoId(ADAPT_CLASS Class, uinT8 ConfigId);
  // Runs the class pruner from int_templates on the given features, returning
  // the number of classes output in results.
  //    int_templates          Class pruner tables
  //    num_features           Number of features in blob
  //    features               Array of features
  //    normalization_factors  (input) Array of int_templates->NumClasses fudge
  //                           factors from blob normalization process.
  //                           (Indexed by CLASS_INDEX)
  //    expected_num_features  (input) Array of int_templates->NumClasses
  //                           expected number of features for each class.
  //                           (Indexed by CLASS_INDEX)
  //    results                (output) Sorted Array of pruned classes.
  //                           Array must be sized to take the maximum possible
  //                           number of outputs : int_templates->NumClasses.
  int PruneClasses(const INT_TEMPLATES_STRUCT* int_templates,
                   int num_features,
                   const INT_FEATURE_STRUCT* features,
                   const uinT8* normalization_factors,
                   const uinT16* expected_num_features,
                   CP_RESULT_STRUCT* results);
  void ReadNewCutoffs(FILE *CutoffFile, bool swap, inT64 end_offset,
                      CLASS_CUTOFF_ARRAY Cutoffs);
  void PrintAdaptedTemplates(FILE *File, ADAPT_TEMPLATES Templates);
  void WriteAdaptedTemplates(FILE *File, ADAPT_TEMPLATES Templates);
  ADAPT_TEMPLATES ReadAdaptedTemplates(FILE *File);
  /* normmatch.cpp ************************************************************/
  FLOAT32 ComputeNormMatch(CLASS_ID ClassId,
                           const FEATURE_STRUCT& feature, BOOL8 DebugMatch);
  void FreeNormProtos();
  NORM_PROTOS *ReadNormProtos(FILE *File, inT64 end_offset);
  /* protos.cpp ***************************************************************/
  void ReadClassFile();
  void ConvertProto(PROTO Proto, int ProtoId, INT_CLASS Class);
  INT_TEMPLATES CreateIntTemplates(CLASSES FloatProtos,
                                   const UNICHARSET& target_unicharset);
  /* adaptmatch.cpp ***********************************************************/

  // Learn the given word using its chopped_word, seam_array, denorm,
  // box_word, best_state, and correct_text to learn both correctly and
  // incorrectly segmented blobs. If filename is not NULL, then LearnBlob
  // is called and the data will be written to a file for static training.
  // Otherwise AdaptToBlob is called for adaption within a document.
  // If rejmap is not NULL, then only chars with a rejmap entry of '1' will
  // be learned, otherwise all chars with good correct_text are learned.
  void LearnWord(const char* filename, const char *rejmap, WERD_RES *word);

  // Builds a blob of length fragments, from the word, starting at start,
  // and then learn it, as having the given correct_text.
  // If filename is not NULL, then LearnBlob
  // is called and the data will be written to a file for static training.
  // Otherwise AdaptToBlob is called for adaption within a document.
  // threshold is a magic number required by AdaptToChar and generated by
  // GetAdaptThresholds.
  // Although it can be partly inferred from the string, segmentation is
  // provided to explicitly clarify the character segmentation.
  void LearnPieces(const char* filename, int start, int length,
                   float threshold, CharSegmentationType segmentation,
                   const char* correct_text, WERD_RES *word);
  void InitAdaptiveClassifier(bool load_pre_trained_templates);
  void InitAdaptedClass(TBLOB *Blob,
                        const DENORM& denorm,
                        CLASS_ID ClassId,
                        int FontinfoId,
                        ADAPT_CLASS Class,
                        ADAPT_TEMPLATES Templates);
  void AdaptToPunc(TBLOB *Blob,
                   const DENORM& denorm,
                   CLASS_ID ClassId,
                   int FontinfoId,
                   FLOAT32 Threshold);
  void AmbigClassifier(TBLOB *Blob,
                       const DENORM& denorm,
                       INT_TEMPLATES Templates,
                       ADAPT_CLASS *Classes,
                       UNICHAR_ID *Ambiguities,
                       ADAPT_RESULTS *Results);
  void MasterMatcher(INT_TEMPLATES templates,
                     inT16 num_features,
                     const INT_FEATURE_STRUCT* features,
                     const uinT8* norm_factors,
                     ADAPT_CLASS* classes,
                     int debug,
                     int num_classes,
                     const TBOX& blob_box,
                     CLASS_PRUNER_RESULTS results,
                     ADAPT_RESULTS* final_results);
  // Converts configs to fonts, and if the result is not adapted, and a
  // shape_table_ is present, the shape is expanded to include all
  // unichar_ids represented, before applying a set of corrections to the
  // distance rating in int_result, (see ComputeCorrectedRating.)
  // The results are added to the final_results output.
  void ExpandShapesAndApplyCorrections(ADAPT_CLASS* classes,
                                       bool debug,
                                       int class_id,
                                       int bottom, int top,
                                       float cp_rating,
                                       int blob_length,
                                       const uinT8* cn_factors,
                                       INT_RESULT_STRUCT& int_result,
                                       ADAPT_RESULTS* final_results);
  // Applies a set of corrections to the distance im_rating,
  // including the cn_correction, miss penalty and additional penalty
  // for non-alnums being vertical misfits. Returns the corrected distance.
  double ComputeCorrectedRating(bool debug, int unichar_id, double cp_rating,
                                double im_rating, int feature_misses,
                                int bottom, int top,
                                int blob_length, const uinT8* cn_factors);
  void ConvertMatchesToChoices(const DENORM& denorm, const TBOX& box,
                               ADAPT_RESULTS *Results,
                               BLOB_CHOICE_LIST *Choices);
  void AddNewResult(ADAPT_RESULTS *results,
                    CLASS_ID class_id,
                    int shape_id,
                    FLOAT32 rating,
                    bool adapted,
                    int config,
                    int fontinfo_id,
                    int fontinfo_id2);
  int GetAdaptiveFeatures(TBLOB *Blob,
                          INT_FEATURE_ARRAY IntFeatures,
                          FEATURE_SET *FloatFeatures);

#ifndef GRAPHICS_DISABLED
  void DebugAdaptiveClassifier(TBLOB *Blob,
                               const DENORM& denorm,
                               ADAPT_RESULTS *Results);
#endif
  void GetAdaptThresholds (TWERD * Word,
                           const DENORM& denorm,
                           const WERD_CHOICE& BestChoice,
                           const WERD_CHOICE& BestRawChoice,
                           FLOAT32 Thresholds[]);

  PROTO_ID MakeNewTempProtos(FEATURE_SET Features,
                             int NumBadFeat,
                             FEATURE_ID BadFeat[],
                             INT_CLASS IClass,
                             ADAPT_CLASS Class,
                             BIT_VECTOR TempProtoMask);
  int MakeNewTemporaryConfig(ADAPT_TEMPLATES Templates,
                             CLASS_ID ClassId,
                             int FontinfoId,
                             int NumFeatures,
                             INT_FEATURE_ARRAY Features,
                             FEATURE_SET FloatFeatures);
  void MakePermanent(ADAPT_TEMPLATES Templates,
                     CLASS_ID ClassId,
                     int ConfigId,
                     const DENORM& denorm,
                     TBLOB *Blob);
  void PrintAdaptiveMatchResults(FILE *File, ADAPT_RESULTS *Results);
  void RemoveExtraPuncs(ADAPT_RESULTS *Results);
  void RemoveBadMatches(ADAPT_RESULTS *Results);
  void SetAdaptiveThreshold(FLOAT32 Threshold);
  void ShowBestMatchFor(TBLOB *Blob,
                        const DENORM& denorm,
                        CLASS_ID ClassId,
                        int shape_id,
                        BOOL8 AdaptiveOn,
                        BOOL8 PreTrainedOn,
                        ADAPT_RESULTS *Results);
  // Returns a string for the classifier class_id: either the corresponding
  // unicharset debug_str or the shape_table_ debug str.
  STRING ClassIDToDebugStr(const INT_TEMPLATES_STRUCT* templates,
                           int class_id, int config_id) const;
  // Converts a classifier class_id index with a config ID to:
  // shape_table_ present: a shape_table_ index OR
  // No shape_table_: a font ID.
  // Without shape training, each class_id, config pair represents a single
  // unichar id/font combination, so this function looks up the corresponding
  // font id.
  // With shape training, each class_id, config pair represents a single
  // shape table index, so the fontset_table stores the shape table index,
  // and the shape_table_ must be consulted to obtain the actual unichar_id/
  // font combinations that the shape represents.
  int ClassAndConfigIDToFontOrShapeID(int class_id,
                                      int int_result_config) const;
  // Converts a shape_table_ index to a classifier class_id index (not a
  // unichar-id!). Uses a search, so not fast.
  int ShapeIDToClassID(int shape_id) const;
  UNICHAR_ID *BaselineClassifier(TBLOB *Blob,
                                 const DENORM& denorm,
                                 ADAPT_TEMPLATES Templates,
                                 ADAPT_RESULTS *Results);
  int CharNormClassifier(TBLOB *Blob,
                         const DENORM& denorm,
                         INT_TEMPLATES Templates,
                         ADAPT_RESULTS *Results);

  // As CharNormClassifier, but operates on a TrainingSample and outputs to
  // a GenericVector of ShapeRating without conversion to classes.
  int CharNormTrainingSample(bool pruner_only, const TrainingSample& sample,
                             GenericVector<ShapeRating>* results);
  UNICHAR_ID *GetAmbiguities(TBLOB *Blob,
                             const DENORM& denorm,
                             CLASS_ID CorrectClass);
  void DoAdaptiveMatch(TBLOB *Blob,
                       const DENORM& denorm,
                       ADAPT_RESULTS *Results);
  void AdaptToChar(TBLOB *Blob,
                   const DENORM& denorm,
                   CLASS_ID ClassId,
                   int FontinfoId,
                   FLOAT32 Threshold);
  void DisplayAdaptedChar(TBLOB* blob, const DENORM& denorm,
                          INT_CLASS_STRUCT* int_class);
  int AdaptableWord(TWERD *Word,
                  const WERD_CHOICE &BestChoiceWord,
                  const WERD_CHOICE &RawChoiceWord);
  void EndAdaptiveClassifier();
  void PrintAdaptiveStatistics(FILE *File);
  void SettupPass1();
  void SettupPass2();
  void AdaptiveClassifier(TBLOB *Blob,
                          const DENORM& denorm,
                          BLOB_CHOICE_LIST *Choices,
                          CLASS_PRUNER_RESULTS cp_results);
  void ClassifyAsNoise(ADAPT_RESULTS *Results);
  void ResetAdaptiveClassifierInternal();

  int GetBaselineFeatures(TBLOB *Blob,
                          const DENORM& denorm,
                          INT_TEMPLATES Templates,
                          INT_FEATURE_ARRAY IntFeatures,
                          uinT8* CharNormArray,
                          inT32 *BlobLength);
  int GetCharNormFeatures(TBLOB *Blob,
                          const DENORM& denorm,
                          INT_TEMPLATES Templates,
                          INT_FEATURE_ARRAY IntFeatures,
                          uinT8* PrunerNormArray,
                          uinT8* CharNormArray,
                          inT32 *BlobLength,
                          inT32 *FeatureOutlineIndex);
  // Computes the char_norm_array for the unicharset and, if not NULL, the
  // pruner_array as appropriate according to the existence of the shape_table.
  // The norm_feature is deleted as it is almost certainly no longer needed.
  void ComputeCharNormArrays(FEATURE_STRUCT* norm_feature,
                             INT_TEMPLATES_STRUCT* templates,
                             uinT8* char_norm_array,
                             uinT8* pruner_array);

  bool TempConfigReliable(CLASS_ID class_id, const TEMP_CONFIG &config);
  void UpdateAmbigsGroup(CLASS_ID class_id, const DENORM& denorm, TBLOB *Blob);

  void ResetFeaturesHaveBeenExtracted();
  bool AdaptiveClassifierIsFull() { return NumAdaptationsFailed > 0; }
  bool LooksLikeGarbage(const DENORM& denorm, TBLOB *blob);
  void RefreshDebugWindow(ScrollView **win, const char *msg,
                          int y_offset, const TBOX &wbox);
  /* float2int.cpp ************************************************************/
  void ClearCharNormArray(uinT8* char_norm_array);
  void ComputeIntCharNormArray(const FEATURE_STRUCT& norm_feature,
                               uinT8* char_norm_array);
  void ComputeIntFeatures(FEATURE_SET Features, INT_FEATURE_ARRAY IntFeatures);
  /* intproto.cpp *************************************************************/
  INT_TEMPLATES ReadIntTemplates(FILE *File);
  void WriteIntTemplates(FILE *File, INT_TEMPLATES Templates,
                         const UNICHARSET& target_unicharset);
  CLASS_ID GetClassToDebug(const char *Prompt, bool* adaptive_on,
                           bool* pretrained_on, int* shape_id);
  void ShowMatchDisplay();
  /* font detection ***********************************************************/
  UnicityTable<FontInfo>& get_fontinfo_table() {
    return fontinfo_table_;
  }
  UnicityTable<FontSet>& get_fontset_table() {
    return fontset_table_;
  }
  /* mfoutline.cpp ***********************************************************/
  void NormalizeOutlines(LIST Outlines, FLOAT32 *XScale, FLOAT32 *YScale);
  /* outfeat.cpp ***********************************************************/
  FEATURE_SET ExtractOutlineFeatures(TBLOB *Blob);
  /* picofeat.cpp ***********************************************************/
  FEATURE_SET ExtractPicoFeatures(TBLOB *Blob);


  // Member variables.

  // Parameters.
  BOOL_VAR_H(prioritize_division, FALSE,
             "Prioritize blob division over chopping");
  INT_VAR_H(tessedit_single_match, FALSE, "Top choice only from CP");
  BOOL_VAR_H(classify_enable_learning, true, "Enable adaptive classifier");
  INT_VAR_H(classify_debug_level, 0, "Classify debug level");

  /* mfoutline.cpp ***********************************************************/
  /* control knobs used to control normalization of outlines */
  INT_VAR_H(classify_norm_method, character, "Normalization Method   ...");
  double_VAR_H(classify_char_norm_range, 0.2,
             "Character Normalization Range ...");
  double_VAR_H(classify_min_norm_scale_x, 0.0, "Min char x-norm scale ...");
  double_VAR_H(classify_max_norm_scale_x, 0.325, "Max char x-norm scale ...");
  double_VAR_H(classify_min_norm_scale_y, 0.0, "Min char y-norm scale ...");
  double_VAR_H(classify_max_norm_scale_y, 0.325, "Max char y-norm scale ...");

  /* adaptmatch.cpp ***********************************************************/
  BOOL_VAR_H(tess_cn_matching, 0, "Character Normalized Matching");
  BOOL_VAR_H(tess_bn_matching, 0, "Baseline Normalized Matching");
  BOOL_VAR_H(classify_enable_adaptive_matcher, 1, "Enable adaptive classifier");
  BOOL_VAR_H(classify_use_pre_adapted_templates, 0,
             "Use pre-adapted classifier templates");
  BOOL_VAR_H(classify_save_adapted_templates, 0,
             "Save adapted templates to a file");
  BOOL_VAR_H(classify_enable_adaptive_debugger, 0, "Enable match debugger");
  INT_VAR_H(matcher_debug_level, 0, "Matcher Debug Level");
  INT_VAR_H(matcher_debug_flags, 0, "Matcher Debug Flags");
  INT_VAR_H(classify_learning_debug_level, 0, "Learning Debug Level: ");
  double_VAR_H(matcher_good_threshold, 0.125, "Good Match (0-1)");
  double_VAR_H(matcher_great_threshold, 0.0, "Great Match (0-1)");
  double_VAR_H(matcher_perfect_threshold, 0.02, "Perfect Match (0-1)");
  double_VAR_H(matcher_bad_match_pad, 0.15, "Bad Match Pad (0-1)");
  double_VAR_H(matcher_rating_margin, 0.1, "New template margin (0-1)");
  double_VAR_H(matcher_avg_noise_size, 12.0, "Avg. noise blob length: ");
  INT_VAR_H(matcher_permanent_classes_min, 1, "Min # of permanent classes");
  INT_VAR_H(matcher_min_examples_for_prototyping, 3,
            "Reliable Config Threshold");
  INT_VAR_H(matcher_sufficient_examples_for_prototyping, 5,
            "Enable adaption even if the ambiguities have not been seen");
  double_VAR_H(matcher_clustering_max_angle_delta, 0.015,
               "Maximum angle delta for prototype clustering");
  double_VAR_H(classify_misfit_junk_penalty, 0.0,
               "Penalty to apply when a non-alnum is vertically out of "
               "its expected textline position");
  double_VAR_H(rating_scale, 1.5, "Rating scaling factor");
  double_VAR_H(certainty_scale, 20.0, "Certainty scaling factor");
  double_VAR_H(tessedit_class_miss_scale, 0.00390625,
               "Scale factor for features not used");
  INT_VAR_H(classify_adapt_proto_threshold, 230,
            "Threshold for good protos during adaptive 0-255");
  INT_VAR_H(classify_adapt_feature_threshold, 230,
            "Threshold for good features during adaptive 0-255");
  BOOL_VAR_H(disable_character_fragments, TRUE,
             "Do not include character fragments in the"
             " results of the classifier");
  double_VAR_H(classify_character_fragments_garbage_certainty_threshold, -3.0,
               "Exclude fragments that do not match any whole character"
               " with at least this certainty");
  BOOL_VAR_H(classify_debug_character_fragments, FALSE,
             "Bring up graphical debugging windows for fragments training");
  BOOL_VAR_H(matcher_debug_separate_windows, FALSE,
             "Use two different windows for debugging the matching: "
             "One for the protos and one for the features.");
  STRING_VAR_H(classify_learn_debug_str, "", "Class str to debug learning");

  /* intmatcher.cpp **********************************************************/
  INT_VAR_H(classify_class_pruner_threshold, 229,
            "Class Pruner Threshold 0-255");
  INT_VAR_H(classify_class_pruner_multiplier, 30,
            "Class Pruner Multiplier 0-255:       ");
  INT_VAR_H(classify_cp_cutoff_strength, 7,
            "Class Pruner CutoffStrength:         ");
  INT_VAR_H(classify_integer_matcher_multiplier, 14,
            "Integer Matcher Multiplier  0-255:   ");

  // Use class variables to hold onto built-in templates and adapted templates.
  INT_TEMPLATES PreTrainedTemplates;
  ADAPT_TEMPLATES AdaptedTemplates;

  // Create dummy proto and config masks for use with the built-in templates.
  BIT_VECTOR AllProtosOn;
  BIT_VECTOR PrunedProtos;
  BIT_VECTOR AllConfigsOn;
  BIT_VECTOR AllProtosOff;
  BIT_VECTOR AllConfigsOff;
  BIT_VECTOR TempProtoMask;
  bool EnableLearning;
  /* normmatch.cpp */
  NORM_PROTOS *NormProtos;
  /* font detection ***********************************************************/
  UnicityTable<FontInfo> fontinfo_table_;
  // Without shape training, each class_id, config pair represents a single
  // unichar id/font combination, so each fontset_table_ entry holds font ids
  // for each config in the class.
  // With shape training, each class_id, config pair represents a single
  // shape_table_ index, so the fontset_table_ stores the shape_table_ index,
  // and the shape_table_ must be consulted to obtain the actual unichar_id/
  // font combinations that the shape represents.
  UnicityTable<FontSet> fontset_table_;

  INT_VAR_H(il1_adaption_test, 0, "Dont adapt to i/I at beginning of word");
  BOOL_VAR_H(classify_bln_numeric_mode, 0,
             "Assume the input is numbers [0-9].");

 protected:
  IntegerMatcher im_;
  FEATURE_DEFS_STRUCT feature_defs_;
  // If a shape_table_ is present, it is used to remap classifier output in
  // ExpandShapesAndApplyCorrections. font_ids referenced by configs actually
  // mean an index to the shape_table_ and the choices returned are *all* the
  // shape_table_ entries at that index.
  ShapeTable* shape_table_;

 private:

  Dict dict_;

  /* variables used to hold performance statistics */
  int AdaptiveMatcherCalls;
  int BaselineClassifierCalls;
  int CharNormClassifierCalls;
  int AmbigClassifierCalls;
  int NumWordsAdaptedTo;
  int NumCharsAdaptedTo;
  int NumBaselineClassesTried;
  int NumCharNormClassesTried;
  int NumAmbigClassesTried;
  int NumClassesOutput;
  int NumAdaptationsFailed;

  /* variables used to hold onto extracted features.  This is used
  to map from the old scheme in which baseline features and char norm
  features are extracted separately, to the new scheme in which they
  are extracted at the same time. */
  bool FeaturesHaveBeenExtracted;
  bool FeaturesOK;
  INT_FEATURE_ARRAY BaselineFeatures;
  INT_FEATURE_ARRAY CharNormFeatures;
  INT_FX_RESULT_STRUCT FXInfo;

  // Expected number of features in the class pruner, used to penalize
  // unknowns that have too few features (like a c being classified as e) so
  // it doesn't recognize everything as '@' or '#'.
  // CharNormCutoffs is for the static classifier (with no shapetable).
  // BaselineCutoffs gets a copy of CharNormCutoffs as an estimate of the real
  // value in the adaptive classifier. Both are indexed by unichar_id.
  // shapetable_cutoffs_ provides a similar value for each shape in the
  // shape_table_
  uinT16* CharNormCutoffs;
  uinT16* BaselineCutoffs;
  GenericVector<uinT16> shapetable_cutoffs_;
  ScrollView* learn_debug_win_;
  ScrollView* learn_fragmented_word_debug_win_;
  ScrollView* learn_fragments_debug_win_;
};
}  // namespace tesseract

#endif  // TESSERACT_CLASSIFY_CLASSIFY_H__
