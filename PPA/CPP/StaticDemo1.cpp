#include<iostream>
using namespace std;

class Demo
{
    public:
        int i;
        int j;
        static int x;
    
        Demo()
        {
            this->i = 0;
            this->j = 0;
        }

         Demo(int a, int b)
        {
            this->i = a;
            this->j = b;
        }

        void Fun()          //vid Fun(demo * const this)
        {
            cout<<"Inside Fun\n";
        }

        static void Gun()   //static void Gun()
        {
            cout<<"Inside Gun\n";
        }
};

int Demo :: x = 11;

int main()
{
    Demo::Gun();
    cout<<"Value of X is : "<<Demo::x<<"\n";
 

    return 0;
}