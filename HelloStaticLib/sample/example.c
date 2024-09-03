#include <stdio.h>
#include <stdlib.h>
#include "hello.hellostaticlib.h"

int main(int argc, char **argv) {

  fprintf(stderr, "main\n");
  graal_isolatethread_t *thread = NULL;
  if (graal_create_isolate(NULL, NULL, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        return 1;
  }

  if (argc != 2 + 1) {
     fprintf(stderr, "Error: please specify a, b arguments\n./example a b\n");
     return 1;
  }

  double a = atof(argv[1]);
  double b = atof(argv[2]);
  double resultSum = staticSum(thread, a, b);
  double resultDiff = staticDiff(thread, a, b);
  char* resultText = staticText(thread);

  fprintf(stderr, "Sum: %.1f\nDiff: %.1f\nText: %s\n", resultSum, resultDiff, resultText);

  if (graal_detach_thread(thread) != 0) {
     fprintf(stderr, "graal_detach_thread error\n");
     return 1;
  }

  fprintf(stderr, "Done!\n");
  return 0;
}
