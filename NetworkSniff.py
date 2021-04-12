from scapy.all import *
import platform

error_code_unknown_platform = 1


def main():
    print("Hello World!")
    sniff_loopback_port_packets(5555)


def sniff_loopback_port_packets(port):
    sniffer_filter = "port " + str(port)
    interface = detect_loopback_interface()
    if interface == error_code_unknown_platform:
        print("Error finding OS aborting sniffing on loop back address")
        return
    print("Sniffing local packets")
    sniff(filter=sniffer_filter, iface=interface, prn=analyse_packet)


def analyse_packet(pkt):
    print("Packet found")
    pkt.show()


def detect_loopback_interface():
    print(str(platform.system()))
    if platform.system() == "Linux":
        return "lo"
    if platform.system() == "Windows":
        return "Software Loopback Interface 1"
    return error_code_unknown_platform


if __name__ == "__main__":
    main()
