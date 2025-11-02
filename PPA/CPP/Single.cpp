#include<iostream>
using namespace std;

class Base                      // 8
{
    public:
        int i,j;

        void fun()
        {
            cout<<"Inside Base Fun\n";
        }
};

class Derived : public Base     // 12
{
    public:
        int x;
  
        void gun()
        {
            cout<<"Inside Derived gun\n";
        }
};

int main()
{
    Base bobj;
    Derived dobj;

    cout<<"Size of class object :"<<sizeof(bobj);
    cout<<"Size of class object :"<<sizeof(dobj);


    return 0;
}