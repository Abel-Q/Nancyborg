#include <Arduino.h>

/* Cette classe permet d'émuler un port série afin d'enregistrer
 * une animation dans l'arduino sans devoir la renvoyer
 * par le vrai port série
 */
class AnimationStream : public Stream {
  public:
    AnimationStream() {
      AnimationStream(0, NULL);
    }

    AnimationStream(int code_size, const byte *code_data) {
      setData(code_size, code_data);
    }

    void setData(int code_size, const byte *code_data) {
      this->code_size = code_size;
      this->code_data = code_data;
      this->code_pos = 0;
    }

    int available() {
      return code_size;
    }

    int read() {
      if (!available())
        return -1;
      int ret = peek();
      this->code_pos = (this->code_pos + 1) % this->code_size;
      //Serial.print("read: "); Serial.print(ret); Serial.print(" '"); Serial.print((char)ret); Serial.println("'");
      return ret;
    }

    int peek() {
      if (!available())
        return -1;
      return this->code_data[code_pos] & 0xFF;
    }

    size_t write(uint8_t) {
      return -1;
    }

    void flush() {
    }

  private:
    int code_size;
    const byte* code_data;
    int code_pos;
};
