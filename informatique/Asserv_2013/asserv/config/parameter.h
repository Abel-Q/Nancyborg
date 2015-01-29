#ifndef PARAMETER_H
#define PARAMETER_H

#include <string>

struct Parameter {
    enum Type {
        INT64,
        BOOL,
        DOUBLE
    };

    Parameter(std::string name, int64_t *ptr) : name(name), ptr(ptr), type(INT64) { *int_ptr = 0; }
    Parameter(std::string name, bool *ptr) : name(name), ptr(ptr), type(BOOL) { *bool_ptr = false; }
    Parameter(std::string name, double *ptr) : name(name), ptr(ptr), type(DOUBLE) { *double_ptr = 0; }

    template <typename T>
    void set(T val) const {
        *((T*) ptr) = val;
    }

    void setFromString(std::string value) const {
        switch (type)  {
            case INT64:
                *int_ptr = atoll(value.c_str());
                break;
            case BOOL:
                *bool_ptr = (value == "true" || value == "1");
                break;
            case DOUBLE:
                *double_ptr = atof(value.c_str());
                break;
        }
    }

    template <typename T>
    T get(void) const {
        return *((T*) ptr);
    }

    bool is(const std::string &other) const {
        return other == name;
    }

    const std::string name;
    const union {
        void *ptr;
        int64_t *int_ptr;
        bool *bool_ptr;
        double *double_ptr;
    };

    const Type type;
};

#endif
