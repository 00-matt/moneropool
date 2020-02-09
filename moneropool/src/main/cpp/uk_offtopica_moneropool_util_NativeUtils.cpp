#include "uk_offtopica_moneropool_util_NativeUtils.h"

#include <cstdint>
#include <string>

namespace cryptonote {

struct block {
  std::uint8_t major_version;
  std::uint8_t minor_version;
  std::uint64_t timestamp;
  char prevHash[32];
  std::uint32_t nonce;
  char pad[600];
};

extern bool parse_and_validate_block_from_blob(const std::string &b_blob,
                                               block &b);

extern std::string get_block_hashing_blob(const block &b);

} // namespace cryptonote

using namespace cryptonote;

JNIEXPORT jbyteArray JNICALL
Java_uk_offtopica_moneropool_util_NativeUtils_getHashingBlob(
    JNIEnv *env, jclass clazz, jbyteArray data_obj) {
  (void)clazz;

  jsize data_len = env->GetArrayLength(data_obj);
  jbyte *data = env->GetByteArrayElements(data_obj, nullptr);

  std::string input{reinterpret_cast<char *>(data),
                    static_cast<std::string::size_type>(data_len)};
  std::string output;
  block b{};

  if (!parse_and_validate_block_from_blob(input, b)) {
    jclass ex_class =
        env->FindClass("uk/offtopica/moneropool/util/InvalidBlockException");
    env->ThrowNew(ex_class, "Failed to parse block");
    return nullptr;
  }

  output = get_block_hashing_blob(b);

  jbyteArray ret = env->NewByteArray(output.size());
  env->SetByteArrayRegion(ret, 0, output.size(),
                          reinterpret_cast<const jbyte *>(output.data()));

  env->ReleaseByteArrayElements(data_obj, data, JNI_ABORT);

  return ret;
}
