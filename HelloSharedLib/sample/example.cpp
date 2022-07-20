#include <iostream>
#include "hello.hellosharedlib.h"

using namespace std;

int main(int argc, char **argv) {
  graal_isolatethread_t *thread = NULL;
  if (graal_create_isolate(NULL, NULL, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        return 1;
  }

  if (argc != 2 + 1) {
     std::cerr << "please specify a, b\n./example a b" << std::endl;
     return 1;
  }

  double a = stod(argv[1]);
  double b = stod(argv[2]);
  double resultSum = sharedSum(thread, a, b);
  double resultDiff = sharedDiff(thread, a, b);
  char* resultText = sharedText(thread);

  std::cout << "Sum: " << resultSum << std::endl;
  std::cout << "Diff: " << resultDiff << std::endl;
  std::cout << "Text: " << resultText << std::endl;

  if (graal_detach_thread(thread) != 0) {
     fprintf(stderr, "graal_detach_thread error\n");
     return 1;
  }

  return 0;
}
