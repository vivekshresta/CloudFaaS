import matplotlib.pyplot as plt
from google.cloud import storage
import io

client = storage.Client(project='vivekshresta-bandaru')
bucket = client.bucket('vivekshresta-faas-bucket')


def plot(data, title):
    # f, ax = plt.subplots(figsize=fig_size)
    fig, ax = plt.subplots()
    ax.hist(x=data, bins='auto', color='#0504aa', alpha=0.7, rwidth=0.85)
    ax.grid(axis='y', alpha=0.75)
    plt.xlabel('Sentence lengths')
    plt.ylabel('Frequency')
    plt.title('Histogram of Sentence Lengths for' + title)
    imageURL = 'histogram_' + str(title[:-3]) + 'png'
    blob = bucket.blob(imageURL)

    buf = io.BytesIO()
    plt.savefig(buf, format='png')

    # upload buffer contents to gcs
    blob.upload_from_string(
        buf.getvalue(),
        content_type='image/png')

    buf.close()
    return blob.public_url


def histogram_function(request):
    request_json = request.get_json(silent=True)
    request_args = request.args

    if request_json and 'frequencies' in request_json:
        data = eval(request_json['frequencies'].strip())
        fileName = request_json['url'].strip()
    elif request_args and 'frequencies' in request_args:
        data = eval(request_args['frequencies'].strip())
        fileName = request_json['url'].strip()
    else:
        return '{"result":"Relevant data not provided"}'

    imageURL = ""
    try:
        imageURL = plot(data, fileName)
    except:
        print("Exception occurred")

    return '{"histogramURL":"' + imageURL + '"}'
