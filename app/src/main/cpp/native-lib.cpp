#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_edgedetectionapp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetectionapp_MainActivity_processImageJNI(JNIEnv *env, jobject thiz,
                                                               jlong mat_addr_input,
                                                               jlong mat_addr_output) {
    // TODO: implement processImageJNI()
}